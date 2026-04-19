package com.football.boardgame.repository;

import com.football.boardgame.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findByCompetitionId(UUID competitionId);
    
    @org.springframework.data.jpa.repository.Query("SELECT m FROM Match m WHERE m.competition.season.id = :seasonId AND m.status = 'IN_PROGRESS'")
    java.util.Optional<Match> findActiveMatchBySeason(UUID seasonId);

    @org.springframework.data.jpa.repository.Query("SELECT m FROM Match m WHERE m.competition.season.id = :seasonId ORDER BY m.round ASC, m.matchDate ASC")
    List<Match> findFixtureBySeason(UUID seasonId);

    @org.springframework.data.jpa.repository.Query("SELECT m FROM Match m WHERE m.competition.season.id = :seasonId AND m.round = :round ORDER BY m.matchDate ASC")
    List<Match> findFixtureBySeasonAndRound(UUID seasonId, Integer round);

    boolean existsByCompetitionId(UUID competitionId);
}
