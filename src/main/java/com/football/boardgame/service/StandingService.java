package com.football.boardgame.service;

import com.football.boardgame.dto.StandingsDTO;
import com.football.boardgame.mapper.StandingsMapper;
import com.football.boardgame.repository.StandingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandingService {

    private final StandingsRepository standingsRepository;
    private final StandingsMapper standingsMapper;

    @Transactional(readOnly = true)
    public List<StandingsDTO> getStandingsByCompetition(UUID competitionId) {
        return standingsRepository.findByCompetitionIdOrderByPositionAsc(competitionId).stream()
                .map(standingsMapper::toDto)
                .collect(Collectors.toList());
    }

    // This would be triggered by MatchService or a separate batch job
    @Transactional
    public void recalculateStandings(UUID competitionId) {
        // Implementation for recalculating based on match results
        // For now, it's a placeholder
    }
}
