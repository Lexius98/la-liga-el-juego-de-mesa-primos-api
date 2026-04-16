package com.football.boardgame.repository;

import com.football.boardgame.domain.SeasonMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeasonMembershipRepository extends JpaRepository<SeasonMembership, UUID> {
    List<SeasonMembership> findBySeasonId(UUID seasonId);
    List<SeasonMembership> findByManagerId(UUID managerId);
}
