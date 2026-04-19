package com.football.boardgame.service;

import com.football.boardgame.domain.*;
import com.football.boardgame.dto.GoalRequestDTO;
import com.football.boardgame.dto.MatchDTO;
import com.football.boardgame.dto.MatchEventDTO;
import com.football.boardgame.mapper.MatchEventMapper;
import com.football.boardgame.mapper.MatchMapper;
import com.football.boardgame.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final PlayerRepository playerRepository;
    private final ManagerRepository managerRepository;
    private final GoalRequestRepository goalRequestRepository;
    private final MatchMapper matchMapper;
    private final MatchEventMapper matchEventMapper;
    private final StandingService standingService;
    private final SimpMessagingTemplate messagingTemplate;

    // ── Consultas ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MatchDTO> getMatchesByCompetition(UUID competitionId) {
        return matchRepository.findByCompetitionId(competitionId).stream()
                .map(matchMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MatchDTO getMatchById(UUID matchId) {
        return matchRepository.findById(matchId)
                .map(matchMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Match not found"));
    }

    @Transactional(readOnly = true)
    public MatchDTO getActiveMatchBySeason(UUID seasonId) {
        return matchRepository.findActiveMatchBySeason(seasonId)
                .map(matchMapper::toDto)
                .orElse(null);
    }

    // ── Ciclo de vida del partido ──────────────────────────────────────────────

    /**
     * Actualiza el marcador del partido (admin).
     * No finaliza el partido — solo actualiza el resultado en curso.
     */
    @Transactional
    public MatchDTO updateScore(UUID matchId, int homeScore, int awayScore) {
        Match match = findOrThrow(matchId);
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setStatus(Match.MatchStatus.IN_PROGRESS);
        Match saved = matchRepository.save(match);

        MatchDTO dto = matchMapper.toDto(saved);
        broadcast(saved, "match_update", dto);
        return dto;
    }

    /**
     * Finaliza un partido: marca como FINISHED y recalcula standings.
     * Disparado por el admin tras introducir el resultado final.
     */
    @Transactional
    public MatchDTO finishMatch(UUID matchId) {
        Match match = findOrThrow(matchId);
        if (match.getStatus() == Match.MatchStatus.FINISHED) {
            throw new IllegalStateException("El partido ya está finalizado.");
        }
        match.setStatus(Match.MatchStatus.FINISHED);
        Match saved = matchRepository.save(match);

        // Recalcular standings automáticamente
        UUID competitionId = saved.getCompetition().getId();
        standingService.recalculateStandings(competitionId);

        MatchDTO dto = matchMapper.toDto(saved);
        broadcast(saved, "match_finished", dto);

        // Broadcast standings actualizadas
        if (saved.getCompetition().getSeason() != null) {
            UUID seasonId = saved.getCompetition().getSeason().getId();
            messagingTemplate.convertAndSend(
                    "/topic/season/" + seasonId + "/standings",
                    Map.of("type", "standings_updated"));
        }

        log.info("[MatchService] Partido {} finalizado: {}-{}", matchId,
                saved.getHomeScore(), saved.getAwayScore());
        return dto;
    }

    /**
     * Resultado completo (mantiene compatibilidad con API existente).
     */
    @Transactional
    public MatchDTO updateMatchResult(UUID matchId, MatchDTO matchDTO) {
        Match match = findOrThrow(matchId);
        if (matchDTO.getHomeScore() != null) match.setHomeScore(matchDTO.getHomeScore());
        if (matchDTO.getAwayScore() != null) match.setAwayScore(matchDTO.getAwayScore());
        if (matchDTO.getStatus() != null) match.setStatus(Match.MatchStatus.valueOf(matchDTO.getStatus()));
        Match saved = matchRepository.save(match);
        MatchDTO dto = matchMapper.toDto(saved);
        broadcast(saved, "match_start", dto);
        return dto;
    }

    // ── Eventos ───────────────────────────────────────────────────────────────

    @Transactional
    public MatchEventDTO addMatchEvent(UUID matchId, MatchEventDTO eventDTO) {
        Match match = findOrThrow(matchId);
        MatchEvent event = matchEventMapper.toEntity(eventDTO);
        event.setMatch(match);

        if (eventDTO.getPlayerId() != null) {
            Player player = playerRepository.findById(eventDTO.getPlayerId())
                    .orElseThrow(() -> new RuntimeException("Player not found"));
            event.setPlayer(player);
            if (event.getType() == MatchEvent.MatchEventType.GOAL) {
                player.setGoalsScored((player.getGoalsScored() != null ? player.getGoalsScored() : 0) + 1);
                playerRepository.save(player);
                if (event.getDescription() == null) {
                    event.setDescription("Gol de " + player.getName() + " (min. " + event.getMinute() + ")");
                }
            }
        } else if (event.getType() == MatchEvent.MatchEventType.GOAL && event.getDescription() == null) {
            event.setDescription("Gol (min. " + event.getMinute() + ")");
        }

        MatchEvent saved = matchEventRepository.save(event);
        MatchEventDTO dto = matchEventMapper.toDto(saved);
        broadcast(match, "match_event", dto);
        return dto;
    }

    // ── Goal Request (manager → admin) ────────────────────────────────────────

    /**
     * Manager solicita la validación de un gol al admin.
     * Broadcast via STOMP al canal de admin.
     */
    @Transactional
    public GoalRequestDTO createGoalRequest(UUID matchId, GoalRequestDTO requestDTO) {
        Match match = findOrThrow(matchId);

        Manager manager = managerRepository.findById(requestDTO.getRequestingManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        // Determinar equipo del manager en este partido
        Team team = null;
        if (match.getHomeTeam().getManager() != null &&
                match.getHomeTeam().getManager().getId().equals(manager.getId())) {
            team = match.getHomeTeam();
        } else if (match.getAwayTeam().getManager() != null &&
                match.getAwayTeam().getManager().getId().equals(manager.getId())) {
            team = match.getAwayTeam();
        }
        if (team == null) {
            throw new IllegalStateException("El manager no es participante de este partido.");
        }

        GoalRequest goalRequest = GoalRequest.builder()
                .match(match)
                .requestingManager(manager)
                .team(team)
                .minute(requestDTO.getMinute())
                .description(requestDTO.getDescription())
                .status(GoalRequest.GoalRequestStatus.PENDING)
                .build();

        if (requestDTO.getPlayerId() != null) {
            playerRepository.findById(requestDTO.getPlayerId())
                    .ifPresent(goalRequest::setPlayer);
        }

        GoalRequest saved = goalRequestRepository.save(goalRequest);
        GoalRequestDTO dto = toGoalRequestDTO(saved);

        // Notificar al admin via STOMP
        if (match.getCompetition().getSeason() != null) {
            UUID seasonId = match.getCompetition().getSeason().getId();
            messagingTemplate.convertAndSend(
                    "/topic/season/" + seasonId + "/admin",
                    Map.of("type", "goal_request", "payload", dto));
        }

        log.info("[GoalRequest] Solicitud de gol en partido {} por manager {} (min. {})",
                matchId, manager.getId(), requestDTO.getMinute());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<GoalRequestDTO> getPendingGoalRequests(UUID matchId) {
        return goalRequestRepository.findByMatchIdAndStatus(matchId, GoalRequest.GoalRequestStatus.PENDING)
                .stream().map(this::toGoalRequestDTO).collect(Collectors.toList());
    }

    /** Admin aprueba la solicitud: crea el evento de gol y recalcula standings. */
    @Transactional
    public GoalRequestDTO approveGoalRequest(UUID matchId, UUID requestId) {
        GoalRequest req = goalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("GoalRequest not found"));

        if (req.getStatus() != GoalRequest.GoalRequestStatus.PENDING) {
            throw new IllegalStateException("La solicitud ya fue procesada.");
        }

        req.setStatus(GoalRequest.GoalRequestStatus.APPROVED);
        goalRequestRepository.save(req);

        // Crear el evento de gol
        MatchEventDTO eventDTO = MatchEventDTO.builder()
                .matchId(matchId)
                .minute(req.getMinute())
                .type("GOAL")
                .teamId(req.getTeam().getId())
                .playerId(req.getPlayer() != null ? req.getPlayer().getId() : null)
                .description(req.getDescription())
                .build();
        addMatchEvent(matchId, eventDTO);

        // Actualizar marcador
        Match match = findOrThrow(matchId);
        if (req.getTeam().getId().equals(match.getHomeTeam().getId())) {
            match.setHomeScore((match.getHomeScore() != null ? match.getHomeScore() : 0) + 1);
        } else {
            match.setAwayScore((match.getAwayScore() != null ? match.getAwayScore() : 0) + 1);
        }
        matchRepository.save(match);

        // Notificar resultado al lobby
        broadcast(match, "goal_approved", Map.of(
                "teamId", req.getTeam().getId(),
                "minute", req.getMinute(),
                "homeScore", match.getHomeScore(),
                "awayScore", match.getAwayScore()));

        return toGoalRequestDTO(req);
    }

    /** Admin rechaza la solicitud. Notifica solo al manager solicitante. */
    @Transactional
    public GoalRequestDTO rejectGoalRequest(UUID matchId, UUID requestId) {
        GoalRequest req = goalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("GoalRequest not found"));

        if (req.getStatus() != GoalRequest.GoalRequestStatus.PENDING) {
            throw new IllegalStateException("La solicitud ya fue procesada.");
        }

        req.setStatus(GoalRequest.GoalRequestStatus.REJECTED);
        goalRequestRepository.save(req);

        Match match = findOrThrow(matchId);
        if (match.getCompetition().getSeason() != null) {
            UUID seasonId = match.getCompetition().getSeason().getId();
            UUID managerId = req.getRequestingManager().getId();
            messagingTemplate.convertAndSend(
                    "/topic/season/" + seasonId + "/manager/" + managerId,
                    Map.of("type", "goal_rejected", "requestId", requestId));
        }

        return toGoalRequestDTO(req);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Match findOrThrow(UUID matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
    }

    private void broadcast(Match match, String type, Object payload) {
        if (match.getCompetition() != null && match.getCompetition().getSeason() != null) {
            UUID seasonId = match.getCompetition().getSeason().getId();
            Map<String, Object> message = new HashMap<>();
            message.put("type", type);
            message.put("payload", payload);
            messagingTemplate.convertAndSend("/topic/season/" + seasonId + "/lobby", message);
        }
    }

    private GoalRequestDTO toGoalRequestDTO(GoalRequest req) {
        return GoalRequestDTO.builder()
                .id(req.getId())
                .matchId(req.getMatch().getId())
                .requestingManagerId(req.getRequestingManager().getId())
                .requestingManagerName(req.getRequestingManager().getName())
                .teamId(req.getTeam().getId())
                .teamName(req.getTeam().getName())
                .playerId(req.getPlayer() != null ? req.getPlayer().getId() : null)
                .playerName(req.getPlayer() != null ? req.getPlayer().getName() : null)
                .minute(req.getMinute())
                .description(req.getDescription())
                .status(req.getStatus().name())
                .build();
    }
}
