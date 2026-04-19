package com.football.boardgame.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequestDTO {
    private UUID id;
    private UUID matchId;
    private UUID requestingManagerId;
    private String requestingManagerName;
    private UUID teamId;
    private String teamName;
    private UUID playerId;
    private String playerName;
    private Integer minute;
    private String description;
    private String status;  // PENDING | APPROVED | REJECTED
}
