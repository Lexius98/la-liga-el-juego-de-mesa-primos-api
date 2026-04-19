package com.football.boardgame.mapper;

import com.football.boardgame.domain.Manager;
import com.football.boardgame.dto.ManagerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManagerMapper {

    @Mapping(target = "teamIds", ignore = true)
    @Mapping(target = "roles", expression = "java(manager.getRoles() == null ? java.util.List.of(\"PLAYER\") : manager.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "global_stats.matches_played", source = "matchesPlayed")
    @Mapping(target = "global_stats.competitions_won", source = "competitionsWon")
    @Mapping(target = "global_stats.wins", source = "wins")
    @Mapping(target = "global_stats.draws", source = "draws")
    @Mapping(target = "global_stats.losses", source = "losses")
    @Mapping(target = "global_stats.goals_for", source = "goalsFor")
    @Mapping(target = "global_stats.goals_against", source = "goalsAgainst")
    ManagerDTO toDto(Manager manager);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "matchesPlayed", source = "global_stats.matches_played")
    @Mapping(target = "competitionsWon", source = "global_stats.competitions_won")
    @Mapping(target = "wins", source = "global_stats.wins")
    @Mapping(target = "draws", source = "global_stats.draws")
    @Mapping(target = "losses", source = "global_stats.losses")
    @Mapping(target = "goalsFor", source = "global_stats.goals_for")
    @Mapping(target = "goalsAgainst", source = "global_stats.goals_against")
    Manager toEntity(ManagerDTO dto);
}

