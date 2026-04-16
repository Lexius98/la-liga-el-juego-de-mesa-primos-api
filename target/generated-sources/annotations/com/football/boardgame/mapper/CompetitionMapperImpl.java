package com.football.boardgame.mapper;

import com.football.boardgame.domain.Competition;
import com.football.boardgame.domain.Season;
import com.football.boardgame.dto.CompetitionDTO;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T10:00:05+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class CompetitionMapperImpl implements CompetitionMapper {

    @Override
    public CompetitionDTO toDto(Competition competition) {
        if ( competition == null ) {
            return null;
        }

        CompetitionDTO competitionDTO = new CompetitionDTO();

        competitionDTO.setSeasonId( competitionSeasonId( competition ) );
        competitionDTO.setId( competition.getId() );
        competitionDTO.setName( competition.getName() );
        if ( competition.getStatus() != null ) {
            competitionDTO.setStatus( competition.getStatus().name() );
        }
        if ( competition.getType() != null ) {
            competitionDTO.setType( competition.getType().name() );
        }

        return competitionDTO;
    }

    @Override
    public Competition toEntity(CompetitionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Competition competition = new Competition();

        competition.setId( dto.getId() );
        competition.setName( dto.getName() );
        if ( dto.getStatus() != null ) {
            competition.setStatus( Enum.valueOf( Competition.CompetitionStatus.class, dto.getStatus() ) );
        }
        if ( dto.getType() != null ) {
            competition.setType( Enum.valueOf( Competition.CompetitionType.class, dto.getType() ) );
        }

        return competition;
    }

    private UUID competitionSeasonId(Competition competition) {
        if ( competition == null ) {
            return null;
        }
        Season season = competition.getSeason();
        if ( season == null ) {
            return null;
        }
        UUID id = season.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
