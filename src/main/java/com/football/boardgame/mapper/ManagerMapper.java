package com.football.boardgame.mapper;

import com.football.boardgame.domain.Manager;
import com.football.boardgame.dto.ManagerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManagerMapper {

    @Mapping(target = "teamIds", ignore = true) // Will be handled in service/custom mapping
    @Mapping(target = "global_stats.matches_played", source = "matchesPlayed")
    @Mapping(target = "global_stats.competitions_won", source = "competitionsWon")
    ManagerDTO toDto(Manager manager);

    @Mapping(target = "matchesPlayed", source = "global_stats.matches_played")
    @Mapping(target = "competitionsWon", source = "global_stats.competitions_won")
    Manager toEntity(ManagerDTO dto);
}
