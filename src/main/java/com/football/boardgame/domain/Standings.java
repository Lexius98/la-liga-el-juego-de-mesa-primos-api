package com.football.boardgame.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "standings")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Standings extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder.Default
    @Column(name = "matches_played")
    private Integer matchesPlayed = 0;

    @Builder.Default
    private Integer wins = 0;
    @Builder.Default
    private Integer draws = 0;
    @Builder.Default
    private Integer losses = 0;

    @Builder.Default
    @Column(name = "goals_for")
    private Integer goalsFor = 0;

    @Builder.Default
    @Column(name = "goals_against")
    private Integer goalsAgainst = 0;

    @Builder.Default
    @Column(name = "goal_difference")
    private Integer goalDifference = 0;

    @Builder.Default
    private Integer points = 0;

    private Integer position;
}
