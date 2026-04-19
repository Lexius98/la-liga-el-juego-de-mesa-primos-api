package com.football.boardgame.mapper;

import com.football.boardgame.domain.Match;
import com.football.boardgame.dto.MatchDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface MatchMapper {

    @Mapping(target = "competitionId", source = "competition.id")
    @Mapping(target = "homeTeamId", source = "homeTeam.id")
    @Mapping(target = "homeTeamName", source = "homeTeam.name")
    @Mapping(target = "awayTeamId", source = "awayTeam.id")
    @Mapping(target = "awayTeamName", source = "awayTeam.name")
    MatchDTO toDto(Match match);

    @Mapping(target = "competition", ignore = true)
    @Mapping(target = "homeTeam", ignore = true)
    @Mapping(target = "awayTeam", ignore = true)
    Match toEntity(MatchDTO dto);
}
