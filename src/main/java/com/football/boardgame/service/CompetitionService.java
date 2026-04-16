package com.football.boardgame.service;

import com.football.boardgame.domain.Competition;
import com.football.boardgame.dto.CompetitionDTO;
import com.football.boardgame.mapper.CompetitionMapper;
import com.football.boardgame.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CompetitionMapper competitionMapper;

    @Transactional(readOnly = true)
    public List<CompetitionDTO> getCompetitionsBySeason(UUID seasonId) {
        return competitionRepository.findBySeasonId(seasonId).stream()
                .map(competitionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompetitionDTO getCompetition(UUID competitionId) {
        return competitionRepository.findById(competitionId)
                .map(competitionMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Competition not found"));
    }

    @Transactional
    public CompetitionDTO createCompetition(CompetitionDTO competitionDTO) {
        Competition competition = competitionMapper.toEntity(competitionDTO);
        Competition savedCompetition = competitionRepository.save(competition);
        return competitionMapper.toDto(savedCompetition);
    }
}
