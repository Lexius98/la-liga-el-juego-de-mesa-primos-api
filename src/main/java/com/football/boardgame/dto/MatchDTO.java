package com.football.boardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {
    private UUID id;
    private UUID competitionId;
    private UUID homeTeamId;
    private String homeTeamName;
    private UUID awayTeamId;
    private String awayTeamName;
    private Integer homeScore;
    private Integer awayScore;
    private String status;
    private LocalDateTime matchDate;
    private UUID nextMatchId;
    /** Número de jornada. Pausa invernal tras jornada = maxTeams - 1. */
    private Integer round;
    /** MANAGER = partido entre managers humanos | NPC = simulado */
    private String matchType;
}
