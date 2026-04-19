package com.football.boardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonDTO {
    private UUID id;
    private String name;
    private String status;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private List<ParticipantDTO> participants;
    private CurrentPhaseDTO current_phase;
    private UUID game_version_id;
    private String lobbyCode;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantDTO {
        private UUID manager_id;
        private UUID team_id;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentPhaseDTO {
        private String type;
        private String label;
        private String competition;
        private Integer matchday_index;
    }
}
