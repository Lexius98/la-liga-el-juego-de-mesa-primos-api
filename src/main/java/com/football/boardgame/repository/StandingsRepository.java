package com.football.boardgame.repository;

import com.football.boardgame.domain.Standings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StandingsRepository extends JpaRepository<Standings, UUID> {
    List<Standings> findByCompetitionIdOrderByPositionAsc(UUID competitionId);
}
