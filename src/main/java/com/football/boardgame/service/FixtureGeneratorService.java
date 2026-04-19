package com.football.boardgame.service;

import com.football.boardgame.domain.Competition;
import com.football.boardgame.domain.Match;
import com.football.boardgame.domain.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * FixtureGeneratorService — Algoritmo Berger Round-Robin
 *
 * Genera el calendario de liga (ida + vuelta) para N equipos (N siempre par).
 *
 * Algoritmo:
 *   - Equipo en posición 0 se mantiene fijo durante toda la rotación.
 *   - Los N-1 equipos restantes rotan una posición a la izquierda en cada ronda.
 *   - Por cada ronda: (fixed vs pool.last) + (pool[i] vs pool[N-2-i]) para i=0..N/2-2
 *   - La vuelta invierte local/visitante de cada partido de ida.
 *
 * Resultado para 20 equipos:
 *   - Primera vuelta:  19 jornadas × 10 partidos = 190 partidos
 *   - Segunda vuelta:  19 jornadas × 10 partidos = 190 partidos
 *   - Total:           38 jornadas, 380 partidos
 *   - Pausa invernal:  tras jornada 19 (fin de primera vuelta)
 */
@Slf4j
@Service
public class FixtureGeneratorService {

    /** Días entre jornadas consecutivas */
    private static final long DAYS_BETWEEN_ROUNDS = 7L;

    /**
     * Genera el fixture completo (ida + vuelta) para la competición dada.
     *
     * @param teams       Lista de equipos participantes (tamaño par, mínimo 2)
     * @param competition Competición a la que pertenecerán los partidos
     * @param seasonStart Fecha de inicio de la temporada (base para calcular fechas)
     * @return Lista de todos los partidos generados (sin persistir)
     */
    public List<Match> generate(List<Team> teams, Competition competition, LocalDateTime seasonStart) {
        validateTeams(teams);

        int n = teams.size();
        int roundsPerLeg = n - 1;           // N-1 jornadas por vuelta
        LocalDateTime baseDate = seasonStart != null ? seasonStart : LocalDateTime.now();

        List<Match> matches = new ArrayList<>(n * (n - 1)); // capacidad exacta

        // Equipo fijo: teams.get(0). Pool: el resto, en LinkedList para rotación O(1)
        Team fixedTeam = teams.get(0);
        LinkedList<Team> pool = new LinkedList<>(teams.subList(1, n));

        // ── Primera vuelta ──────────────────────────────────────────────────────
        for (int r = 0; r < roundsPerLeg; r++) {
            int round = r + 1;
            LocalDateTime matchDate = baseDate.plusDays(r * DAYS_BETWEEN_ROUNDS);

            // Par del equipo fijo vs el último del pool.
            // En rondas impares (r%2==1) el fijo juega fuera — alternancia de localía.
            if (r % 2 == 0) {
                matches.add(buildMatch(fixedTeam, pool.getLast(), competition, round, matchDate));
            } else {
                matches.add(buildMatch(pool.getLast(), fixedTeam, competition, round, matchDate));
            }

            // Pares del pool: pool[i] vs pool[n-3-i]  (i = 0 .. n/2 - 2)
            // El último elemento de pool ya se usó en el par anterior → se omite
            for (int i = 0; i < (n / 2) - 1; i++) {
                Team home = pool.get(i);
                Team away = pool.get(n - 3 - i);
                matches.add(buildMatch(home, away, competition, round, matchDate));
            }

            // Rotación de Berger: mover el primer elemento al final del pool
            pool.addLast(pool.removeFirst());
        }

        // ── Pausa invernal (se guarda en Competition) ────────────────────────────
        competition.setWinterBreakAfterRound(roundsPerLeg);

        // ── Segunda vuelta: invertir local/visitante ─────────────────────────────
        // Los partidos de ida tienen ronda 1..roundsPerLeg → vuelta: ronda+roundsPerLeg
        int firstLegSize = matches.size(); // = roundsPerLeg * (n/2) partidos
        for (int i = 0; i < firstLegSize; i++) {
            Match idaMatch = matches.get(i);
            int returnRound = idaMatch.getRound() + roundsPerLeg;
            LocalDateTime returnDate = idaMatch.getMatchDate()
                    .plusDays(roundsPerLeg * DAYS_BETWEEN_ROUNDS);
            matches.add(buildMatch(
                    idaMatch.getAwayTeam(),
                    idaMatch.getHomeTeam(),
                    competition,
                    returnRound,
                    returnDate
            ));
        }

        log.info("[FixtureGenerator] Generados {} partidos en {} jornadas para competición '{}' ({} equipos)",
                matches.size(), 2 * roundsPerLeg, competition.getName(), n);

        return matches;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Match buildMatch(Team home, Team away, Competition competition, int round, LocalDateTime date) {
        Match.MatchType type = (home.getManager() != null && away.getManager() != null)
                ? Match.MatchType.MANAGER
                : Match.MatchType.NPC;

        return Match.builder()
                .homeTeam(home)
                .awayTeam(away)
                .competition(competition)
                .round(round)
                .matchType(type)
                .status(Match.MatchStatus.SCHEDULED)
                .matchDate(date)
                .homeScore(0)
                .awayScore(0)
                .build();
    }

    private void validateTeams(List<Team> teams) {
        if (teams == null || teams.size() < 2) {
            throw new IllegalArgumentException(
                "Se necesitan al menos 2 equipos para generar el fixture. Equipos recibidos: "
                + (teams == null ? 0 : teams.size()));
        }
        if (teams.size() % 2 != 0) {
            throw new IllegalArgumentException(
                "El número de equipos debe ser par para el formato Round-Robin. " +
                "Equipos recibidos: " + teams.size() +
                ". Considera añadir un equipo de descanso (BYE) si el número es impar.");
        }
    }
}
