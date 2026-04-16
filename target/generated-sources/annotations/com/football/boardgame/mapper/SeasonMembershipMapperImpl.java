package com.football.boardgame.mapper;

import com.football.boardgame.domain.Manager;
import com.football.boardgame.domain.Season;
import com.football.boardgame.domain.SeasonMembership;
import com.football.boardgame.domain.Team;
import com.football.boardgame.dto.SeasonMembershipDTO;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T10:00:05+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class SeasonMembershipMapperImpl implements SeasonMembershipMapper {

    @Override
    public SeasonMembershipDTO toDto(SeasonMembership membership) {
        if ( membership == null ) {
            return null;
        }

        SeasonMembershipDTO seasonMembershipDTO = new SeasonMembershipDTO();

        seasonMembershipDTO.setSeasonId( membershipSeasonId( membership ) );
        seasonMembershipDTO.setManagerId( membershipManagerId( membership ) );
        seasonMembershipDTO.setClubId( membershipTeamId( membership ) );
        seasonMembershipDTO.setId( membership.getId() );
        seasonMembershipDTO.setJoinedAt( membership.getJoinedAt() );
        if ( membership.getStatus() != null ) {
            seasonMembershipDTO.setStatus( membership.getStatus().name() );
        }

        return seasonMembershipDTO;
    }

    @Override
    public SeasonMembership toEntity(SeasonMembershipDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SeasonMembership seasonMembership = new SeasonMembership();

        seasonMembership.setId( dto.getId() );
        seasonMembership.setJoinedAt( dto.getJoinedAt() );
        if ( dto.getStatus() != null ) {
            seasonMembership.setStatus( Enum.valueOf( SeasonMembership.MembershipStatus.class, dto.getStatus() ) );
        }

        return seasonMembership;
    }

    private UUID membershipSeasonId(SeasonMembership seasonMembership) {
        if ( seasonMembership == null ) {
            return null;
        }
        Season season = seasonMembership.getSeason();
        if ( season == null ) {
            return null;
        }
        UUID id = season.getId();
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
