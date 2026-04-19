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
public class LobbyMemberDTO {
    private UUID managerId;
    private String displayName;
    private String avatarUrl;
    private ClubDTO club;
    private String status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClubDTO {
        private UUID id;
        private String name;
        private String shortName;
        private String logoUrl;
    }
}
