package com.football.boardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDTO {
    private UUID id;
    private String googleId;
    private String email;
    private String avatarUrl;
    private String name;
    private List<UUID> teamIds;
    private GlobalStatsDTO global_stats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GlobalStatsDTO {
        private Integer matches_played;
        private Integer competitions_won;
    }
}
