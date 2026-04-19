package com.football.boardgame.mapper;

import com.football.boardgame.domain.Season;
import com.football.boardgame.domain.SeasonMembership;
import com.football.boardgame.dto.SeasonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface SeasonMapper {

    @Mapping(target = "start_date", source = "startDate")
    @Mapping(target = "end_date", source = "endDate")
    @Mapping(target = "participants", source = "memberships")
    @Mapping(target = "game_version_id", source = "gameEdition.id")
    @Mapping(target = "lobbyCode", source = "lobbyCode")
    @Mapping(target = "current_phase", ignore = true)
    SeasonDTO toDto(Season season);

    @Mapping(target = "endDate", source = "end_date")
    @Mapping(target = "memberships", ignore = true) // Handled in service
    @Mapping(target = "gameEdition", ignore = true) // Handled in service
    @Mapping(target = "lobbyCode", source = "lobbyCode")
    Season toEntity(SeasonDTO dto);

    @Mapping(target = "manager_id", source = "manager.id")
    @Mapping(target = "team_id", source = "team.id")
    SeasonDTO.ParticipantDTO toParticipantDto(SeasonMembership membership);
}
