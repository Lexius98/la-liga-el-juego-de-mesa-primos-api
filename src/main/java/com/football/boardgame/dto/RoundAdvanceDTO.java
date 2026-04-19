package com.football.boardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Respuesta del endpoint POST /api/seasons/{id}/matchdays/{round}/advance.
 *
 * Comunica al cliente:
 * - La jornada que se acaba de cerrar y la nueva jornada activa.
 * - Los partidos NPC simulados en el cierre.
 * - Un resumen del estado de la competición (¿hay más jornadas? ¿pausa invernal?).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundAdvanceDTO {

    /** Jornada que se acaba de cerrar */
    private int closedRound;

    /** Nueva jornada activa (0 si se ha completado la liga) */
    private int nextRound;

    /** Total de jornadas de la competición */
    private int totalRounds;

    /** True si la siguiente jornada es la pausa invernal */
    private boolean winterBreakNext;

    /** True si la liga ha terminado (no hay más jornadas) */
    private boolean leagueFinished;

    /** IDs de partidos NPC que se simularon al avanzar */
    private List<UUID> simulatedMatchIds;

    /** ID de la competición */
    private UUID competitionId;
}
