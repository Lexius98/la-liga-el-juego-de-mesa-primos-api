package com.football.boardgame.mapper;

import com.football.boardgame.domain.SeasonMembership;
import com.football.boardgame.dto.LobbyMemberDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LobbyMapper {

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "displayName", source = "manager.name")
    @Mapping(target = "avatarUrl", source = "manager.avatarUrl")
    @Mapping(target = "club.id", source = "team.id")
    @Mapping(target = "club.name", source = "team.name")
    @Mapping(target = "club.shortName", source = "team.shortName")
    @Mapping(target = "club.logoUrl", source = "team.logo")
    @Mapping(target = "status", source = "status")
    LobbyMemberDTO toLobbyDto(SeasonMembership membership);
}
