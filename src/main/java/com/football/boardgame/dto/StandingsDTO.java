package com.football.boardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandingsDTO {
    private UUID id;
    private UUID competitionId;
    private UUID teamId;
    private String teamName;
    private Integer matches_played;
    private Integer wins;
    private Integer draws;
    private Integer losses;
    private Integer goals_for;
    private Integer goals_against;
    private Integer goal_difference;
    private Integer points;
    private Integer position;
}
