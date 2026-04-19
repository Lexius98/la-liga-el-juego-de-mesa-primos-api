package com.football.boardgame.mapper;

import com.football.boardgame.domain.MatchEvent;
import com.football.boardgame.dto.MatchEventDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface MatchEventMapper {

    @Mapping(target = "matchId", source = "match.id")
    @Mapping(target = "playerId", source = "player.id")
    @Mapping(target = "playerName", source = "player.name")
    @Mapping(target = "assistPlayerId", source = "assistPlayer.id")
    @Mapping(target = "teamId", source = "team.id")
    MatchEventDTO toDto(MatchEvent event);

    @Mapping(target = "match", ignore = true)
    @Mapping(target = "player", ignore = true)
    @Mapping(target = "assistPlayer", ignore = true)
    @Mapping(target = "team", ignore = true)
    MatchEvent toEntity(MatchEventDTO dto);
}
