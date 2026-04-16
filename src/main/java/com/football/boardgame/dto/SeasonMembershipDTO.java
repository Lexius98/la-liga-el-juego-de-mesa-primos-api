package com.football.boardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonMembershipDTO {
    private UUID id;
    private UUID seasonId;
    private UUID managerId;
    private UUID clubId;
    private String status;
    private LocalDateTime joinedAt;
}
