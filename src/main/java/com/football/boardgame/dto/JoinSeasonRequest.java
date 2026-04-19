package com.football.boardgame.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class JoinSeasonRequest {
    private UUID managerId;
    private UUID teamId;
}
