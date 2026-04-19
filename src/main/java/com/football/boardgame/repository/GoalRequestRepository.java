package com.football.boardgame.repository;

import com.football.boardgame.domain.GoalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoalRequestRepository extends JpaRepository<GoalRequest, UUID> {

    /** Solicitudes pendientes de un partido — para el admin */
    List<GoalRequest> findByMatchIdAndStatus(UUID matchId, GoalRequest.GoalRequestStatus status);

    /** Todas las solicitudes de un partido */
    List<GoalRequest> findByMatchId(UUID matchId);
}
