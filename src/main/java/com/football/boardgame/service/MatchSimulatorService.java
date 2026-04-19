package com.football.boardgame.service;

import com.football.boardgame.domain.*;
import com.football.boardgame.dto.MatchDTO;
import com.football.boardgame.mapper.MatchMapper;
import com.football.boardgame.repository.MatchEventRepository;
import com.football.boardgame.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * MatchSimulatorService — simula partidos NPC al final de cada jornada.
 *
 * Distribución de goles (realista):
 *   - Media: ~1.45 goles por equipo (total ~2.9/partido)
 *   - Local tiene ~10% de ventaja en probabilidad de marcar
 *   - Distribución aproximada a Poisson mediante CDF pre-calculado
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchSimulatorService {

    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final StandingService standingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MatchMapper matchMapper;

    private static final Random RNG = new Random();

    /**
     * Simula todos los partidos NPC de una jornada específica.
     * Solo simula partidos en estado SCHEDULED con matchType = NPC.
     *
     * @param seasonId ID de la temporada
     * @param round    Número de jornada
     * @return Lista de DTOs de partidos simulados
     */
    @Transactional
    public List<MatchDTO> simulateRound(UUID seasonId, int round) {
        List<Match> npcMatches = matchRepository.findNpcMatchesToSimulate(seasonId, round);

        if (npcMatches.isEmpty()) {
            log.info("[Simulator] No hay partidos NPC pendientes en la jornada {} de la temporada {}", round, seasonId);
            return Collections.emptyList();
        }

        List<MatchDTO> results = new ArrayList<>();
        Set<UUID> competitionIds = new HashSet<>();

        for (Match match : npcMatches) {
            simulateMatch(match);
            competitionIds.add(match.getCompetition().getId());
            results.add(matchMapper.toDto(match));
            log.info("[Simulator] {} {}-{} {} (Jornada {})",
                    match.getHomeTeam().getName(), match.getHomeScore(),
                    match.getAwayScore(), match.getAwayTeam().getName(), round);
        }

        // Recalcular standings de todas las competiciones afectadas
        for (UUID compId : competitionIds) {
            standingService.recalculateStandings(compId);
        }

        // Broadcast resultado de simulación
        String destination = "/topic/season/" + seasonId + "/standings";
        messagingTemplate.convertAndSend(destination, Map.of("type", "standings_updated", "round", round));

        log.info("[Simulator] Jornada {} simulada: {} partidos NPC", round, npcMatches.size());
        return results;
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private void simulateMatch(Match match) {
        int homeGoals = randomGoals(true);   // local: ligera ventaja
        int awayGoals = randomGoals(false);

        match.setHomeScore(homeGoals);
        match.setAwayScore(awayGoals);
        match.setStatus(Match.MatchStatus.FINISHED);
        matchRepository.save(match);

        // Generar eventos GOAL para cada gol
        generateGoalEvents(match, match.getHomeTeam(), homeGoals);
        generateGoalEvents(match, match.getAwayTeam(), awayGoals);
    }

    /**
     * Distribución aproximada a Poisson con lambda local=1.5, away=1.3.
     * P(0)≈22%, P(1)≈33%, P(2)≈25%, P(3)≈13%, P(4+)≈7%
     */
    private int randomGoals(boolean isHome) {
        double lambda = isHome ? 1.5 : 1.3;
        double p = RNG.nextDouble();
        // CDF Poisson pre-calculado
        double[] cdf = poissonCdf(lambda, 5);
        for (int k = 0; k < cdf.length; k++) {
            if (p <= cdf[k]) return k;
        }
        return 5;
    }

    private double[] poissonCdf(double lambda, int maxK) {
        double[] cdf = new double[maxK + 1];
        double sum = 0;
        double fact = 1;
        for (int k = 0; k <= maxK; k++) {
            if (k > 0) fact *= k;
            sum += Math.exp(-lambda) * Math.pow(lambda, k) / fact;
            cdf[k] = sum;
        }
        return cdf;
    }

    private void generateGoalEvents(Match match, Team team, int goals) {
        for (int i = 0; i < goals; i++) {
            int minute = 1 + RNG.nextInt(90);
            MatchEvent event = MatchEvent.builder()
                    .match(match)
                    .team(team)
                    .type(MatchEvent.MatchEventType.GOAL)
                    .minute(minute)
                    .description("Gol de " + team.getName() + " (min. " + minute + ")")
                    .build();
            matchEventRepository.save(event);
        }
    }
}
