package com.football.boardgame.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Plantilla de carta física digitalizada.
 * Representa una carta del juego de mesa (jugador o club) dentro de una GameEdition.
 * Creada a partir del reconocimiento OCR de la foto de la carta.
 */
@Entity
@Table(name = "card_templates",
       uniqueConstraints = @UniqueConstraint(columnNames = {"edition_id", "card_type", "name"}))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CardTemplate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edition_id", nullable = false)
    private GameEdition edition;

    /** PLAYER = carta de jugador, CLUB = carta de equipo/club */
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private CardType cardType;

    /** Nombre completo extraído del OCR o introducido manualmente */
    @Column(nullable = false)
    private String name;

    /** Abreviatura (p.e. "MES", "BCN") */
    @Column(name = "short_name")
    private String shortName;

    /** Posición del jugador: POR, LD, DC, MC, DL... (solo para PLAYER) */
    @Column
    private String position;

    /** Color primario del club en hex (solo para CLUB) */
    @Column(name = "primary_color")
    private String primaryColor;

    /** URL de la imagen/logo si se subió */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Atributos de la carta en formato JSON libre.
     * Ejemplos PLAYER: { "pace": 90, "shooting": 85, "actions": ["Regate", "Disparo"] }
     * Ejemplos CLUB:   { "budget": 80, "prestige": 4 }
     */
    @Column(name = "attributes", columnDefinition = "TEXT")
    private String attributesJson;

    /**
     * Confianza del OCR al extraer los datos (0.0 - 1.0).
     * 1.0 = introducido/confirmado manualmente por el admin.
     */
    @Builder.Default
    @Column(name = "ocr_confidence")
    private Float ocrConfidence = 0.0f;

    /** URL de la imagen original que se usó para el OCR */
    @Column(name = "ocr_source_image_url")
    private String ocrSourceImageUrl;

    public enum CardType {
        PLAYER, CLUB
    }
}
