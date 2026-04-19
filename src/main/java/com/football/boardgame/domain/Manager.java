package com.football.boardgame.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "managers")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Manager extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String googleId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column
    private String avatarUrl;

    /**
     * Rol del manager en el sistema.
     * ADMIN  → puede gestionar ediciones, roles y toda la configuracion.
     * SCANNER → puede escanear y registrar cartas en el catalogo de una edicion.
     * PLAYER  → rol por defecto, solo puede participar en temporadas.
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ManagerRole role = ManagerRole.PLAYER;

    public enum ManagerRole {
        ADMIN, SCANNER, PLAYER
    }

    @Builder.Default
    @Column(name = "matches_played")
    private Integer matchesPlayed = 0;

    @Builder.Default
    @Column(name = "competitions_won")
    private Integer competitionsWon = 0;

    @Builder.Default
    @Column(name = "wins")
    private Integer wins = 0;

    @Builder.Default
    @Column(name = "draws")
    private Integer draws = 0;

    @Builder.Default
    @Column(name = "losses")
    private Integer losses = 0;

    @Builder.Default
    @Column(name = "goals_for")
    private Integer goalsFor = 0;

    @Builder.Default
    @Column(name = "goals_against")
    private Integer goalsAgainst = 0;
}
