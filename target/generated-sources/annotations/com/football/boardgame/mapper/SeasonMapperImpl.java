package com.football.boardgame.mapper;

import com.football.boardgame.domain.GameEdition;
import com.football.boardgame.domain.Manager;
import com.football.boardgame.domain.Season;
import com.football.boardgame.domain.SeasonMembership;
import com.football.boardgame.domain.Team;
import com.football.boardgame.dto.SeasonDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T10:00:05+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class SeasonMapperImpl implements SeasonMapper {

    @Override
    public SeasonDTO toDto(Season season) {
        if ( season == null ) {
            return null;
        }

        SeasonDTO seasonDTO = new SeasonDTO();

        seasonDTO.setStart_date( season.getStartDate() );
        seasonDTO.setEnd_date( season.getEndDate() );
        seasonDTO.setParticipants( seasonMembershipListToParticipantDTOList( season.getMemberships() ) );
        seasonDTO.setGame_version_id( seasonGameEditionId( season ) );
        seasonDTO.setLobby_code( season.getLobbyCode() );
        seasonDTO.setId( season.getId() );
        seasonDTO.setName( season.getName() );
        if ( season.getStatus() != null ) {
            seasonDTO.setStatus( season.getStatus().name() );
        }

        return seasonDTO;
    }

    @Override
    public Season toEntity(SeasonDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Season season = new Season();

        season.setEndDate( dto.getEnd_date() );
        season.setLobbyCode( dto.getLobby_code() );
        season.setId( dto.getId() );
        season.setName( dto.getName() );
        if ( dto.getStatus() != null ) {
            season.setStatus( Enum.valueOf( Season.SeasonStatus.class, dto.getStatus() ) );
        }

        return season;
    }

    @Override
    public SeasonDTO.ParticipantDTO toParticipantDto(SeasonMembership membership) {
        if ( membership == null ) {
            return null;
        }

        SeasonDTO.ParticipantDTO participantDTO = new SeasonDTO.ParticipantDTO();

        participantDTO.setManager_id( membershipManagerId( membership ) );
        participantDTO.setTeam_id( membershipTeamId( membership ) );

        return participantDTO;
    }

    protected List<SeasonDTO.ParticipantDTO> seasonMembershipListToParticipantDTOList(List<SeasonMembership> list) {
        if ( list == null ) {
            return null;
        }

        List<SeasonDTO.ParticipantDTO> list1 = new ArrayList<SeasonDTO.ParticipantDTO>( list.size() );
        for ( SeasonMembership seasonMembership : list ) {
            list1.add( toParticipantDto( seasonMembership ) );
        }

        return list1;
    }

    private UUID seasonGameEditionId(Season season) {
        if ( season == null ) {
            return null;
        }
        GameEdition gameEdition = season.getGameEdition();
        if ( gameEdition == null ) {
            return null;
        }
        UUID id = gameEdition.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID membershipManagerId(SeasonMembership seasonMembership) {
        if ( seasonMembership == null ) {
            return null;
        }
        Manager manager = seasonMembership.getManager();
        if ( manager == null ) {
            return null;
        }
        UUID id = manager.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID membershipTeamId(SeasonMembership seasonMembership) {
        if ( seasonMembership == null ) {
            return null;
        }
        Team team = seasonMembership.getTeam();
        if ( team == null ) {
            return null;
        }
        UUID id = team.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
