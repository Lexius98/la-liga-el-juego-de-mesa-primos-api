package com.football.boardgame.mapper;

import com.football.boardgame.domain.Standings;
import com.football.boardgame.dto.StandingsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface StandingsMapper {

    @Mapping(target = "competitionId", source = "competition.id")
    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "matches_played", source = "matchesPlayed")
    @Mapping(target = "goals_for", source = "goalsFor")
    @Mapping(target = "goals_against", source = "goalsAgainst")
    @Mapping(target = "goal_difference", source = "goalDifference")
    StandingsDTO toDto(Standings standings);

    @Mapping(target = "competition", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "matchesPlayed", source = "matches_played")
    @Mapping(target = "goalsFor", source = "goals_for")
    @Mapping(target = "goalsAgainst", source = "goals_against")
    @Mapping(target = "goalDifference", source = "goal_difference")
    Standings toEntity(StandingsDTO dto);
}
