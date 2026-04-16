package com.football.boardgame.service;

import com.football.boardgame.domain.Season;
import com.football.boardgame.domain.Team;
import com.football.boardgame.dto.TeamDTO;
import com.football.boardgame.mapper.TeamMapper;
import com.football.boardgame.repository.SeasonRepository;
import com.football.boardgame.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final SeasonRepository seasonRepository;
    private final TeamMapper teamMapper;

    @Transactional(readOnly = true)
    public List<TeamDTO> getTeamsBySeason(UUID seasonId) {
        return teamRepository.findBySeasonId(seasonId).stream()
                .map(teamMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @org.springframework.lang.NonNull
    public TeamDTO createOrUpdateTeam(TeamDTO teamDTO) {
        Team team = teamMapper.toEntity(teamDTO);
        
        // Handle Season association if competitionId is provider as seasonId in some contexts
        UUID competitionId = teamDTO.getCompetitionId();
        if (competitionId != null) {
            Season season = seasonRepository.findById(competitionId)
                    .orElseThrow(() -> new RuntimeException("Season not found"));
            team.setSeason(season);
        }
        
        Team savedTeam = teamRepository.save(team);
        TeamDTO result = teamMapper.toDto(savedTeam);
        if (result == null) {
            throw new RuntimeException("Error mapping saved team to DTO");
        }
        return result;
    }
}
