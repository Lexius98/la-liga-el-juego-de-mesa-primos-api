package com.football.boardgame.repository;

import com.football.boardgame.domain.OcrDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OcrDictionaryRepository extends JpaRepository<OcrDictionary, UUID> {

    Optional<OcrDictionary> findByTermTypeAndRawText(
            OcrDictionary.TermType termType, String rawText);

    /** Devuelve todos los términos de un tipo ordenados por frecuencia. */
    List<OcrDictionary> findByTermTypeOrderByOccurrencesDesc(OcrDictionary.TermType termType);

    /** Busca los mejores matches para un rawText usando LIKE (soporte fuzzy básico). */
    @Query("SELECT d FROM OcrDictionary d WHERE d.termType = :termType " +
           "AND LOWER(d.rawText) LIKE LOWER(CONCAT('%', :rawText, '%')) " +
           "ORDER BY d.occurrences DESC")
    List<OcrDictionary> findBestMatches(
            @org.springframework.data.repository.query.Param("termType") OcrDictionary.TermType termType,
            @org.springframework.data.repository.query.Param("rawText") String rawText);
}
