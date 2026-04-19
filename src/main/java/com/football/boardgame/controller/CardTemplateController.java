package com.football.boardgame.controller;

import com.football.boardgame.domain.OcrDictionary;
import com.football.boardgame.dto.CardTemplateDTO;
import com.football.boardgame.service.CardTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * API REST para el catálogo de cartas de una GameEdition (Issue #32).
 *
 * Base path: /api/editions/{editionId}/cards
 */
@RestController
@RequestMapping("/api/editions/{editionId}/cards")
@RequiredArgsConstructor
public class CardTemplateController {

    private final CardTemplateService cardTemplateService;

    // ── Catálogo ─────────────────────────────────────────────────────────────

    /**
     * Devuelve todas las cartas de una edición.
     * GET /api/editions/{editionId}/cards
     * GET /api/editions/{editionId}/cards?type=PLAYER|CLUB
     */
    @GetMapping
    public ResponseEntity<List<CardTemplateDTO>> getCards(
            @PathVariable UUID editionId,
            @RequestParam(required = false) String type) {
        if (type != null && !type.isBlank()) {
            return ResponseEntity.ok(cardTemplateService.getCardsByEditionAndType(editionId, type));
        }
        return ResponseEntity.ok(cardTemplateService.getCardsByEdition(editionId));
    }

    /**
     * Muestra el número de cartas de la edición.
     * GET /api/editions/{editionId}/cards/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCount(@PathVariable UUID editionId) {
        return ResponseEntity.ok(Map.of("count", cardTemplateService.countByEdition(editionId)));
    }

    /**
     * Añade una carta al catálogo (con o sin OCR).
     * Si ya existe (mismo nombre + tipo), la actualiza.
     * POST /api/editions/{editionId}/cards
     */
    @PostMapping
    public ResponseEntity<CardTemplateDTO> addCard(
            @PathVariable UUID editionId,
            @RequestBody CardTemplateDTO dto) {
        dto.setEditionId(editionId);
        return ResponseEntity.ok(cardTemplateService.addCard(editionId, dto));
    }

    /**
     * Carga masiva de cartas desde el escáner (batch).
     * POST /api/editions/{editionId}/cards/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<CardTemplateDTO>> bulkAddCards(
            @PathVariable UUID editionId,
            @RequestBody List<CardTemplateDTO> cards) {
        cards.forEach(dto -> dto.setEditionId(editionId));
        return ResponseEntity.ok(cardTemplateService.bulkAddCards(editionId, cards));
    }

    /**
     * Elimina una carta del catálogo.
     * DELETE /api/editions/{editionId}/cards/{cardId}
     */
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable UUID editionId,
            @PathVariable UUID cardId) {
        cardTemplateService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

    // ── Diccionario OCR (aprendizaje) ─────────────────────────────────────────

    /**
     * Enseña al sistema una correspondencia rawText → normalizedValue.
     * POST /api/editions/{editionId}/cards/learn
     * Body: { "termType": "POSITION", "rawText": "prtero", "normalizedValue": "POR" }
     */
    @PostMapping("/learn")
    public ResponseEntity<Void> learnTerm(
            @PathVariable UUID editionId,
            @RequestBody Map<String, String> body) {
        OcrDictionary.TermType termType = OcrDictionary.TermType.valueOf(body.get("termType").toUpperCase());
        cardTemplateService.learnTerm(termType, body.get("rawText"), body.get("normalizedValue"));
        return ResponseEntity.ok().build();
    }

    /**
     * Devuelve el diccionario de términos aprendidos (para debug o visualización).
     * GET /api/editions/{editionId}/cards/dictionary?termType=POSITION
     */
    @GetMapping("/dictionary")
    public ResponseEntity<List<OcrDictionary>> getDictionary(
            @PathVariable UUID editionId,
            @RequestParam String termType) {
        return ResponseEntity.ok(cardTemplateService.getDictionary(
                OcrDictionary.TermType.valueOf(termType.toUpperCase())));
    }
}
