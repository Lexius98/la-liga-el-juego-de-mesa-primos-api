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
    private UUID awayTeamId;
    private Integer homeScore;
    private Integer awayScore;
    private String status;
    private LocalDateTime matchDate;
    private UUID nextMatchId;
}
