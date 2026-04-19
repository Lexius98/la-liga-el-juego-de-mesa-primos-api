package com.football.boardgame.repository;

import com.football.boardgame.domain.CardTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardTemplateRepository extends JpaRepository<CardTemplate, UUID> {

    List<CardTemplate> findByEditionId(UUID editionId);

    List<CardTemplate> findByEditionIdAndCardType(UUID editionId, CardTemplate.CardType cardType);

    /** Busca por nombre exacto dentro de una edición y tipo (para evitar duplicados). */
    Optional<CardTemplate> findByEditionIdAndCardTypeAndName(
            UUID editionId, CardTemplate.CardType cardType, String name);

    /** Cuenta las cartas de una edición. */
    long countByEditionId(UUID editionId);

    /** Comprueba si ya existe una carta con ese nombre en la edición. */
    boolean existsByEditionIdAndCardTypeAndName(
            UUID editionId, CardTemplate.CardType cardType, String name);
}
