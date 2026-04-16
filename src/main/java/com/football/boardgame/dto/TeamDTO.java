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
public class TeamDTO {
    private UUID id;
    private String name;
    private String shortName;
    private String logo;
    private String description;
    private String location;
    private String primaryColor;
    private String secondaryColor;
    private UUID managerId;
    private UUID competitionId;
}
