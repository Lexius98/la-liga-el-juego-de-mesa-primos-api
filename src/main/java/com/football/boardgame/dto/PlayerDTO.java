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
public class PlayerDTO {
    private UUID id;
    private String name;
    private String position;
    private UUID teamId;
    private Integer goalsScored;
    private Integer assistsMade;
}
