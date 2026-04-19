package com.football.boardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO para crear, actualizar y consultar CardTemplates.
 * El admin puede enviar los datos tras el OCR con sus correcciones.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardTemplateDTO {

    private UUID id;

    private UUID editionId;

    /** "PLAYER" o "CLUB" */
    private String cardType;

    /** Nombre completo de la carta */
    private String name;

    /** Abreviatura (3-4 chars) */
    private String shortName;

    /** Solo para PLAYER: POR, DC, MC, LD, DL... */
    private String position;

    /** Solo para CLUB: color hex primario */
    private String primaryColor;

    /** URL imagen/logo si existe */
    private String imageUrl;

    /**
     * Atributos en formato JSON string.
     * PLAYER: { "pace": 90, "shooting": 85, "actions": ["Regate", "Disparo"] }
     * CLUB:   { "budget": 80, "prestige": 4 }
     */
    private String attributesJson;

    /** Confianza del OCR (0.0 - 1.0). 1.0 = confirmado por admin. */
    private Float ocrConfidence;

    /** URL de la imagen original enviada al OCR */
    private String ocrSourceImageUrl;

    // ── Respuesta del OCR (solo en el preview, antes de confirmar) ──────────

    /**
     * Cuando se envía una imagen para OCR, el backend devuelve este DTO
     * con los campos pre-rellenados y la confianza por campo, para que
     * el admin los revise antes de confirmar.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OcrPreviewDTO {
        /** Campos extraídos del OCR, listos para editar */
        private CardTemplateDTO extractedCard;
        /** Confianza global (0.0 - 1.0) */
        private float confidence;
        /** Sugerencias del diccionario por campo */
        private List<FieldSuggestion> suggestions;
        /** URL temporal de la imagen procesada */
        private String imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldSuggestion {
        private String field;       // "position", "name", "action[0]"...
        private String rawOcr;      // lo que leyó el OCR
        private String suggested;   // lo que sugiere el diccionario
        private float confidence;   // confianza de la sugerencia
    }
}
