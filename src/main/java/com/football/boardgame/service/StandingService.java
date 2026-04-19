package com.football.boardgame.service;

import com.football.boardgame.domain.Match;
import com.football.boardgame.domain.Standings;
import com.football.boardgame.dto.StandingsDTO;
import com.football.boardgame.mapper.StandingsMapper;
import com.football.boardgame.repository.MatchRepository;
import com.football.boardgame.repository.StandingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * StandingService — gestión y recálculo de clasificaciones.
 *
 * Criterios de desempate (MVP):
 *   1. Puntos (Pts)
 *   2. Diferencia de goles (GD)
 *   3. Goles a favor (GF)
 *   Head-to-head: pendiente (#standings-v2)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StandingService {

    private final StandingsRepository standingsRepository;
    private final MatchRepository matchRepository;
    private final StandingsMapper standingsMapper;

    @Transactional(readOnly = true)
    public List<StandingsDTO> getStandingsByCompetition(UUID competitionId) {
        return standingsRepository.findByCompetitionIdOrderByPositionAsc(competitionId).stream()
                .map(standingsMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Recalcula completamente la clasificación de una competición a partir
     * de todos los partidos FINISHED. Se dispara automáticamente al finalizar
     * un partido (manager o NPC).
     *
     * Operación idempotente: puede ejecutarse múltiples veces sin efectos secundarios.
     */
    @Transactional
    public void recalculateStandings(UUID competitionId) {
        // 1. Cargar todas las standings actuales de la competición
        List<Standings> allStandings = standingsRepository.findByCompetitionId(competitionId);
        if (allStandings.isEmpty()) {
            log.warn("[Standings] No hay standings para competition {}", competitionId);
            return;
        }

        // 2. Crear mapa teamId → Standings para acceso rápido
        Map<UUID, Standings> standingsMap = new HashMap<>();
        for (Standings s : allStandings) {
            // Reset completo
            s.setMatchesPlayed(0);
            s.setWins(0);
            s.setDraws(0);
            s.setLosses(0);
            s.setGoalsFor(0);
            s.setGoalsAgainst(0);
            s.setGoalDifference(0);
            s.setPoints(0);
            s.setPosition(null);
            standingsMap.put(s.getTeam().getId(), s);
        }

        // 3. Acumular resultados de partidos FINISHED
        List<Match> finishedMatches = matchRepository.findByCompetitionIdAndStatus(
                competitionId, Match.MatchStatus.FINISHED);

        for (Match match : finishedMatches) {
            UUID homeId = match.getHomeTeam().getId();
            UUID awayId = match.getAwayTeam().getId();
            int homeGoals = match.getHomeScore() != null ? match.getHomeScore() : 0;
            int awayGoals = match.getAwayScore() != null ? match.getAwayScore() : 0;

            Standings home = standingsMap.get(homeId);
            Standings away = standingsMap.get(awayId);

            if (home == null || away == null) continue; // Equipo NPC sin standings

            home.setMatchesPlayed(home.getMatchesPlayed() + 1);
            away.setMatchesPlayed(away.getMatchesPlayed() + 1);

            home.setGoalsFor(home.getGoalsFor() + homeGoals);
            home.setGoalsAgainst(home.getGoalsAgainst() + awayGoals);
            away.setGoalsFor(away.getGoalsFor() + awayGoals);
            away.setGoalsAgainst(away.getGoalsAgainst() + homeGoals);

            if (homeGoals > awayGoals) {
                // Victoria local
                home.setWins(home.getWins() + 1);
                home.setPoints(home.getPoints() + 3);
                away.setLosses(away.getLosses() + 1);
            } else if (homeGoals < awayGoals) {
                // Victoria visitante
                away.setWins(away.getWins() + 1);
                away.setPoints(away.getPoints() + 3);
                home.setLosses(home.getLosses() + 1);
            } else {
                // Empate
                home.setDraws(home.getDraws() + 1);
                home.setPoints(home.getPoints() + 1);
                away.setDraws(away.getDraws() + 1);
                away.setPoints(away.getPoints() + 1);
            }
        }

        // 4. Calcular GD y ordenar: Pts DESC → GD DESC → GF DESC
        List<Standings> sorted = allStandings.stream()
                .peek(s -> s.setGoalDifference(s.getGoalsFor() - s.getGoalsAgainst()))
                .sorted(Comparator
                        .comparingInt(Standings::getPoints).reversed()
                        .thenComparingInt(Standings::getGoalDifference).reversed()
                        .thenComparingInt(Standings::getGoalsFor).reversed())
                .collect(Collectors.toList());

        // 5. Asignar posiciones 1..N
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setPosition(i + 1);
        }

        standingsRepository.saveAll(sorted);
        log.info("[Standings] Recalculadas {} posiciones para competition {}", sorted.size(), competitionId);
    }
}
