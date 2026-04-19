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
    private String playerName;
    private UUID assistPlayerId;
    private UUID teamId;
    /** Descripción del evento. Auto-generada o introducida manualmente. */
    private String description;
}
