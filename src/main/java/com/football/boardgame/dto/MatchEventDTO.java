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
public class MatchEventDTO {
    private UUID id;
    private UUID matchId;
    private Integer minute;
    private String type;
    private UUID playerId;
    private UUID assistPlayerId;
}
