package com.football.boardgame.mapper;

import com.football.boardgame.domain.Player;
import com.football.boardgame.dto.PlayerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface PlayerMapper {

    @Mapping(target = "teamId", source = "team.id")
    PlayerDTO toDto(Player player);

    @Mapping(target = "team", ignore = true)
    Player toEntity(PlayerDTO dto);
}
