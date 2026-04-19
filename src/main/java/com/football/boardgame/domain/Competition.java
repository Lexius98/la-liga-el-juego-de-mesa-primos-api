package com.football.boardgame.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competitions")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Competition extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompetitionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private Season season;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompetitionStatus status;

    /** Número máximo de equipos. Siempre par. Default 20. */
    @Builder.Default
    @Column(name = "max_teams")
    private Integer maxTeams = 20;

    /**
     * Jornada tras la que se produce la pausa invernal.
     * Se calcula al generar el fixture: maxTeams - 1.
     * Null hasta que el fixture sea generado.
     */
    @Column(name = "winter_break_after_round")
    private Integer winterBreakAfterRound;

    /**
     * Jornada actualmente activa (la que los managers están jugando).
     * El admin avanza manualmente al llamar a POST /seasons/{id}/matchdays/{round}/advance.
     * 0 = fixture generado pero sin empezar, 1 = primera jornada activa, etc.
     */
    @Builder.Default
    @Column(name = "current_round")
    private Integer currentRound = 0;

    @Builder.Default
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Standings> standings = new ArrayList<>();

    public enum CompetitionType {
        LEAGUE, KNOCKOUT
    }

    public enum CompetitionStatus {
        UPCOMING, ACTIVE, COMPLETED
    }
}
