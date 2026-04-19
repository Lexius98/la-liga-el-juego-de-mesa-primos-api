package com.football.boardgame.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "matches")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Match extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    @Builder.Default
    @Column(name = "home_score")
    private Integer homeScore = 0;

    @Builder.Default
    @Column(name = "away_score")
    private Integer awayScore = 0;

    /** Número de jornada dentro de la competición (1 = primera, N-1 = fin de ida, 2*(N-1) = fin de vuelta) */
    @Column(nullable = false)
    private Integer round;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    @Column(name = "next_match_id")
    private UUID nextMatchId; // For Knockout Brackets

    public enum MatchStatus {
        SCHEDULED, IN_PROGRESS, FINISHED, POSTPONED
    }
}
