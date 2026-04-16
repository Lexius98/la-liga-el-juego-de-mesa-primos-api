package com.football.boardgame.mapper;

import com.football.boardgame.domain.SeasonMembership;
import com.football.boardgame.dto.SeasonMembershipDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface SeasonMembershipMapper {

    @Mapping(target = "seasonId", source = "season.id")
    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "clubId", source = "team.id")
    SeasonMembershipDTO toDto(SeasonMembership membership);

    @Mapping(target = "season", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "team", ignore = true)
    SeasonMembership toEntity(SeasonMembershipDTO dto);
}
