package com.football.boardgame.service;

import com.football.boardgame.domain.CardTemplate;
import com.football.boardgame.domain.GameEdition;
import com.football.boardgame.domain.OcrDictionary;
import com.football.boardgame.dto.CardTemplateDTO;
import com.football.boardgame.repository.CardTemplateRepository;
import com.football.boardgame.repository.GameEditionRepository;
import com.football.boardgame.repository.OcrDictionaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Motor de catálogo de cartas (Issue #32).
 *
 * Gestiona la creación, consulta y eliminación de CardTemplates
 * vinculadas a una GameEdition. Detecta duplicados y enriquece
 * el diccionario OCR con las correcciones confirmadas por el admin.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CardTemplateService {

    private final CardTemplateRepository cardTemplateRepository;
    private final OcrDictionaryRepository ocrDictionaryRepository;
    private final GameEditionRepository gameEditionRepository;

    // ── CRUD básico ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CardTemplateDTO> getCardsByEdition(UUID editionId) {
        return cardTemplateRepository.findByEditionId(editionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CardTemplateDTO> getCardsByEditionAndType(UUID editionId, String type) {
        CardTemplate.CardType cardType = CardTemplate.CardType.valueOf(type.toUpperCase());
        return cardTemplateRepository.findByEditionIdAndCardType(editionId, cardType).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countByEdition(UUID editionId) {
        return cardTemplateRepository.countByEditionId(editionId);
    }

    /**
     * Añade una carta al catálogo de una edición.
     * Si ya existe (mismo nombre + tipo + edición), actualiza los datos.
     * Tras confirmar, aprende del diccionario OCR.
     */
    @Transactional
    public CardTemplateDTO addCard(UUID editionId, CardTemplateDTO dto) {
        GameEdition edition = gameEditionRepository.findById(editionId)
                .orElseThrow(() -> new RuntimeException("GameEdition not found: " + editionId));

        CardTemplate.CardType cardType = CardTemplate.CardType.valueOf(dto.getCardType().toUpperCase());

        // Dedup: si ya existe, actualiza
        Optional<CardTemplate> existing = cardTemplateRepository
                .findByEditionIdAndCardTypeAndName(editionId, cardType, dto.getName());

        CardTemplate card = existing.orElseGet(() -> CardTemplate.builder()
                .edition(edition)
                .cardType(cardType)
                .build());

        card.setName(dto.getName());
        card.setShortName(dto.getShortName());
        card.setPosition(dto.getPosition());
        card.setPrimaryColor(dto.getPrimaryColor());
        card.setImageUrl(dto.getImageUrl());
        card.setAttributesJson(dto.getAttributesJson());
        card.setOcrConfidence(dto.getOcrConfidence() != null ? dto.getOcrConfidence() : 1.0f);
        card.setOcrSourceImageUrl(dto.getOcrSourceImageUrl());

        CardTemplate saved = cardTemplateRepository.save(card);
        log.info("[#32] Carta guardada: {} ({}) en edicion '{}'", saved.getName(), cardType, edition.getName());

        // Aprender del diccionario (solo si fue confirmada con correcciones OCR)
        if (dto.getOcrSourceImageUrl() != null && dto.getOcrConfidence() != null && dto.getOcrConfidence() < 1.0f) {
            learnFromConfirmation(dto);
        }

        return toDto(saved);
    }

    /**
     * Carga masiva de cartas (batch desde el escáner).
     * Retorna solo las añadidas/actualizadas con éxito.
     */
    @Transactional
    public List<CardTemplateDTO> bulkAddCards(UUID editionId, List<CardTemplateDTO> cards) {
        return cards.stream()
                .map(dto -> {
                    try {
                        return addCard(editionId, dto);
                    } catch (Exception e) {
                        log.warn("[#32] Error añadiendo carta '{}': {}", dto.getName(), e.getMessage());
                        return null;
                    }
                })
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    /**
     * Elimina una carta del catálogo (solo ADMIN).
     */
    @Transactional
    public void deleteCard(UUID cardId) {
        cardTemplateRepository.deleteById(cardId);
        log.info("[#32] Carta eliminada: {}", cardId);
    }

    // ── Diccionario OCR (aprendizaje progresivo) ─────────────────────────────

    /**
     * Registra una corrección confirmada por el admin en el diccionario.
     * La próxima vez que el OCR lea el mismo rawText, el sistema sugerirá el normalizedValue.
     */
    @Transactional
    public void learnTerm(OcrDictionary.TermType termType, String rawText, String normalizedValue) {
        Optional<OcrDictionary> existing = ocrDictionaryRepository
                .findByTermTypeAndRawText(termType, rawText);

        if (existing.isPresent()) {
            OcrDictionary entry = existing.get();
            entry.setNormalizedValue(normalizedValue); // el admin puede corregir la sugerencia
            entry.setOccurrences(entry.getOccurrences() + 1);
            entry.setLastSeenAt(LocalDateTime.now());
            ocrDictionaryRepository.save(entry);
        } else {
            ocrDictionaryRepository.save(OcrDictionary.builder()
                    .termType(termType)
                    .rawText(rawText.toLowerCase())
                    .normalizedValue(normalizedValue)
                    .occurrences(1)
                    .lastSeenAt(LocalDateTime.now())
                    .build());
        }
        log.debug("[OCR Dictionary] Aprendido: '{}' -> '{}' ({})", rawText, normalizedValue, termType);
    }

    /**
     * Dado un texto OCR, busca el mejor match en el diccionario.
     * Retorna null si no hay match con suficiente confianza.
     */
    @Transactional(readOnly = true)
    public String suggest(OcrDictionary.TermType termType, String rawText) {
        return ocrDictionaryRepository.findByTermTypeAndRawText(termType, rawText.toLowerCase())
                .map(OcrDictionary::getNormalizedValue)
                .orElseGet(() -> {
                    List<OcrDictionary> fuzzy = ocrDictionaryRepository
                            .findBestMatches(termType, rawText);
                    return fuzzy.isEmpty() ? null : fuzzy.get(0).getNormalizedValue();
                });
    }

    /** Devuelve todo el diccionario de un tipo de término, ordenado por frecuencia. */
    @Transactional(readOnly = true)
    public List<OcrDictionary> getDictionary(OcrDictionary.TermType termType) {
        return ocrDictionaryRepository.findByTermTypeOrderByOccurrencesDesc(termType);
    }

    // ── Mapper interno ───────────────────────────────────────────────────────

    private CardTemplateDTO toDto(CardTemplate c) {
        return CardTemplateDTO.builder()
                .id(c.getId())
                .editionId(c.getEdition() != null ? c.getEdition().getId() : null)
                .cardType(c.getCardType().name())
                .name(c.getName())
                .shortName(c.getShortName())
                .position(c.getPosition())
                .primaryColor(c.getPrimaryColor())
                .imageUrl(c.getImageUrl())
                .attributesJson(c.getAttributesJson())
                .ocrConfidence(c.getOcrConfidence())
                .ocrSourceImageUrl(c.getOcrSourceImageUrl())
                .build();
    }

    /** Aprende de los campos de la carta confirmada. */
    private void learnFromConfirmation(CardTemplateDTO dto) {
        if (dto.getPosition() != null) {
            learnTerm(OcrDictionary.TermType.POSITION, dto.getPosition(), dto.getPosition());
        }
        if (dto.getName() != null) {
            OcrDictionary.TermType type = "CLUB".equals(dto.getCardType())
                    ? OcrDictionary.TermType.CLUB_NAME
                    : OcrDictionary.TermType.PLAYER_NAME;
            learnTerm(type, dto.getName(), dto.getName());
        }
    }
}
