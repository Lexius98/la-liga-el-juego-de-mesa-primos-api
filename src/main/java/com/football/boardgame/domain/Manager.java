package com.football.boardgame.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

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
     * Roles del manager en el sistema. Un manager puede tener varios roles a la vez.
     * ADMIN  → gestiona ediciones, roles y configuración global.
     * SCANNER → puede escanear y registrar cartas en el catálogo de una edición.
     * PLAYER  → participa en temporadas como manager de un equipo.
     *
     * La tabla física es manager_roles(manager_id, role).
     * Por defecto todo nuevo manager tiene el rol PLAYER.
     */
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "manager_roles", joinColumns = @JoinColumn(name = "manager_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<ManagerRole> roles = new HashSet<>(Set.of(ManagerRole.PLAYER));

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
