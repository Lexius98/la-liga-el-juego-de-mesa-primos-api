package com.football.boardgame.mapper;

import com.football.boardgame.domain.Club;
import com.football.boardgame.dto.ClubDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClubMapper {
    ClubDTO toDto(Club club);
    Club toEntity(ClubDTO dto);
}
