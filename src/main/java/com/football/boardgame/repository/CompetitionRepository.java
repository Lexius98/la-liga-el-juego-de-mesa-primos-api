package com.football.boardgame.repository;

import com.football.boardgame.domain.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, UUID> {
    List<Competition> findBySeasonId(UUID seasonId);
}
