package com.football.boardgame.mapper;

import com.football.boardgame.domain.Competition;
import com.football.boardgame.dto.CompetitionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface CompetitionMapper {

    @Mapping(target = "seasonId", source = "season.id")
    CompetitionDTO toDto(Competition competition);

    @Mapping(target = "season", ignore = true)
    Competition toEntity(CompetitionDTO dto);
}
