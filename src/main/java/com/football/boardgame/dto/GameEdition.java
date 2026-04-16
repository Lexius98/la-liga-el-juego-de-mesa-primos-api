package com.football.boardgame.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class GameEdition {
    private UUID id;
    private String name;
    private String description;
    private Double startingBudget;
}