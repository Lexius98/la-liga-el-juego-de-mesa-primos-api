package com.football.boardgame.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Diccionario de términos aprendidos por el sistema OCR.
 * Cada vez que un admin confirma o corrige un campo de una carta,
 * el sistema guarda la correspondencia rawText → normalizedValue.
 *
 * En escaneos posteriores, el sistema sugiere el match con mayor frecuencia
 * para el mismo rawText, mejorando la precisión progresivamente.
 */
@Entity
@Table(name = "ocr_dictionary",
       uniqueConstraints = @UniqueConstraint(columnNames = {"term_type", "raw_text"}))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OcrDictionary extends BaseEntity {

    /** Tipo de término: posición, acción, nombre de club, nombre de jugador... */
    @Enumerated(EnumType.STRING)
    @Column(name = "term_type", nullable = false)
    private TermType termType;

    /** Texto tal como lo reconoció el OCR (puede tener errores tipográficos) */
    @Column(name = "raw_text", nullable = false)
    private String rawText;

    /** Valor correcto confirmado por el admin */
    @Column(name = "normalized_value", nullable = false)
    private String normalizedValue;

    /** Veces que esta correspondencia ha sido confirmada */
    @Builder.Default
    @Column(name = "occurrences")
    private Integer occurrences = 1;

    /** Última vez que fue vista/confirmada */
    @Builder.Default
    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt = LocalDateTime.now();

    public enum TermType {
        POSITION,       // POR, DC, MC, LD, DL, MP...
        ACTION,         // Regate, Disparo, Cabeza, Pase largo...
        CLUB_NAME,      // Barcelona, Real Madrid...
        PLAYER_NAME,    // Messi, Cristiano...
        ATTRIBUTE_KEY   // pace, shooting, passing...
    }
}
