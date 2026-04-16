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
public class CompetitionDTO {
    private UUID id;
    private UUID seasonId;
    private String name;
    private String type;
    private String status;
}
