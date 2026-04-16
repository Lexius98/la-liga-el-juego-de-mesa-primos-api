package com.football.boardgame.repository;

import com.football.boardgame.domain.GameEdition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface GameEditionRepository extends JpaRepository<GameEdition, UUID> {
}