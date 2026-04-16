package com.football.boardgame.mapper;

import com.football.boardgame.domain.Manager;
import com.football.boardgame.domain.Team;
import com.football.boardgame.dto.TeamDTO;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T10:00:05+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class TeamMapperImpl implements TeamMapper {

    @Override
    public TeamDTO toDto(Team team) {
        if ( team == null ) {
            return null;
        }

        TeamDTO teamDTO = new TeamDTO();

        teamDTO.setManagerId( teamManagerId( team ) );
        teamDTO.setDescription( team.getDescription() );
        teamDTO.setId( team.getId() );
        teamDTO.setLocation( team.getLocation() );
        teamDTO.setLogo( team.getLogo() );
        teamDTO.setName( team.getName() );
        teamDTO.setPrimaryColor( team.getPrimaryColor() );
        teamDTO.setSecondaryColor( team.getSecondaryColor() );
        teamDTO.setShortName( team.getShortName() );

        return teamDTO;
    }

    @Override
    public Team toEntity(TeamDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Team team = new Team();

        team.setId( dto.getId() );
        team.setDescription( dto.getDescription() );
        team.setLocation( dto.getLocation() );
        team.setLogo( dto.getLogo() );
        team.setName( dto.getName() );
        team.setPrimaryColor( dto.getPrimaryColor() );
        team.setSecondaryColor( dto.getSecondaryColor() );
        team.setShortName( dto.getShortName() );

        return team;
    }

    private UUID teamManagerId(Team team) {
        if ( team == null ) {
            return null;
        }
        Manager manager = team.getManager();
        if ( manager == null ) {
            return null;
        }
        UUID id = manager.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
