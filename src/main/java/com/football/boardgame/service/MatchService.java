package com.football.boardgame.service;

import com.football.boardgame.domain.Match;
import com.football.boardgame.domain.MatchEvent;
import com.football.boardgame.domain.Player;
import com.football.boardgame.dto.MatchDTO;
import com.football.boardgame.dto.MatchEventDTO;
import com.football.boardgame.mapper.MatchEventMapper;
import com.football.boardgame.mapper.MatchMapper;
import com.football.boardgame.repository.MatchEventRepository;
import com.football.boardgame.repository.MatchRepository;
import com.football.boardgame.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final PlayerRepository playerRepository;
    private final MatchMapper matchMapper;
    private final MatchEventMapper matchEventMapper;
    private final SimpMessagingTemplate messagingTemplate;

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

    @Transactional
    public MatchDTO updateMatchResult(UUID matchId, MatchDTO matchDTO) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        
        match.setHomeScore(matchDTO.getHomeScore());
        match.setAwayScore(matchDTO.getAwayScore());
        
        if (matchDTO.getStatus() != null) {
            match.setStatus(Match.MatchStatus.valueOf(matchDTO.getStatus()));
        }
        
        Match savedMatch = matchRepository.save(match);
        MatchDTO resultDto = matchMapper.toDto(savedMatch);
        
        // Broadcast update to mobile apps
        broadcastUpdate(savedMatch, "match_start", resultDto);
        
        return resultDto;
    }

    @Transactional
    public MatchEventDTO addMatchEvent(UUID matchId, MatchEventDTO eventDTO) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        
        MatchEvent event = matchEventMapper.toEntity(eventDTO);
        event.setMatch(match);
        
        UUID playerId = eventDTO.getPlayerId();
        if (playerId != null) {
            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new RuntimeException("Player not found"));
            event.setPlayer(player);
            
            // Increment goals if it's a goal event
            if (event.getType() == MatchEvent.MatchEventType.GOAL) {
                Integer currentGoals = player.getGoalsScored();
                player.setGoalsScored((currentGoals != null ? currentGoals : 0) + 1);
                playerRepository.save(player);
            }
        }
        
        MatchEvent savedEvent = matchEventRepository.save(event);
        MatchEventDTO resultDto = matchEventMapper.toDto(savedEvent);
        
        // Broadcast event to mobile apps
        broadcastUpdate(match, "match_event", resultDto);
        
        return resultDto;
    }

    private void broadcastUpdate(Match match, String type, Object payload) {
        if (match.getCompetition() != null && match.getCompetition().getSeason() != null) {
            UUID seasonId = match.getCompetition().getSeason().getId();
            String destination = "/topic/season/" + seasonId + "/lobby";
            
            // Format matching what the mobile app expects (LobbyMessage)
            java.util.Map<String, Object> message = new java.util.HashMap<>();
            message.put("type", type);
            message.put("payload", payload);
            
            messagingTemplate.convertAndSend(destination, message);
        }
    }
}
