package com.football.boardgame.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * GoalRequest — solicitud de gol enviada por un manager.
 * El admin del escritorio la aprueba o rechaza.
 * Al aprobar se crea el MatchEvent correspondiente y se recalculan standings.
 */
@Entity
@Table(name = "goal_requests")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_manager_id", nullable = false)
    private Manager requestingManager;

    /** Equipo que marca el gol (el equipo del manager solicitante) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /** Jugador que marca (opcional — puede no haber jugadores registrados aún) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(nullable = false)
    private Integer minute;

    /** Contexto del gol según el manager (libre) */
    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private GoalRequestStatus status = GoalRequestStatus.PENDING;

    public enum GoalRequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
