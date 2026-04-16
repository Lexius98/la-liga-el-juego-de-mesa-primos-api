package com.football.boardgame.mapper;

import com.football.boardgame.domain.Team;
import com.football.boardgame.dto.TeamDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface TeamMapper {

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "competitionId", ignore = true) // To be handled based on context
    TeamDTO toDto(Team team);

    @Mapping(target = "manager", ignore = true) // Handled in service
    @Mapping(target = "season", ignore = true)  // Handled in service
    Team toEntity(TeamDTO dto);
}
