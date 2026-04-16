package com.football.boardgame.mapper;

import com.football.boardgame.domain.Club;
import com.football.boardgame.dto.ClubDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T10:00:04+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ClubMapperImpl implements ClubMapper {

    @Override
    public ClubDTO toDto(Club club) {
        if ( club == null ) {
            return null;
        }

        ClubDTO.ClubDTOBuilder clubDTO = ClubDTO.builder();

        clubDTO.competitionTags( club.getCompetitionTags() );
        clubDTO.description( club.getDescription() );
        clubDTO.id( club.getId() );
        clubDTO.location( club.getLocation() );
        clubDTO.logo( club.getLogo() );
        clubDTO.name( club.getName() );
        clubDTO.primaryColor( club.getPrimaryColor() );
        clubDTO.secondaryColor( club.getSecondaryColor() );
        clubDTO.shortName( club.getShortName() );

        return clubDTO.build();
    }

    @Override
    public Club toEntity(ClubDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Club.ClubBuilder<?, ?> club = Club.builder();

        club.id( dto.getId() );
        club.competitionTags( dto.getCompetitionTags() );
        club.description( dto.getDescription() );
        club.location( dto.getLocation() );
        club.logo( dto.getLogo() );
        club.name( dto.getName() );
        club.primaryColor( dto.getPrimaryColor() );
        club.secondaryColor( dto.getSecondaryColor() );
        club.shortName( dto.getShortName() );

        return club.build();
    }
}
