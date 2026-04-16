package com.football.boardgame.mapper;

import com.football.boardgame.domain.Competition;
import com.football.boardgame.domain.Match;
import com.football.boardgame.domain.Team;
import com.football.boardgame.dto.MatchDTO;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T10:00:05+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MatchMapperImpl implements MatchMapper {

    @Override
    public MatchDTO toDto(Match match) {
        if ( match == null ) {
            return null;
        }

        MatchDTO matchDTO = new MatchDTO();

        matchDTO.setCompetitionId( matchCompetitionId( match ) );
        matchDTO.setHomeTeamId( matchHomeTeamId( match ) );
        matchDTO.setAwayTeamId( matchAwayTeamId( match ) );
        matchDTO.setAwayScore( match.getAwayScore() );
        matchDTO.setHomeScore( match.getHomeScore() );
        matchDTO.setId( match.getId() );
        matchDTO.setMatchDate( match.getMatchDate() );
        matchDTO.setNextMatchId( match.getNextMatchId() );
        if ( match.getStatus() != null ) {
            matchDTO.setStatus( match.getStatus().name() );
        }

        return matchDTO;
    }

    @Override
    public Match toEntity(MatchDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Match match = new Match();

        match.setId( dto.getId() );
        match.setAwayScore( dto.getAwayScore() );
        match.setHomeScore( dto.getHomeScore() );
        match.setMatchDate( dto.getMatchDate() );
        match.setNextMatchId( dto.getNextMatchId() );
        if ( dto.getStatus() != null ) {
            match.setStatus( Enum.valueOf( Match.MatchStatus.class, dto.getStatus() ) );
        }

        return match;
    }

    private UUID matchCompetitionId(Match match) {
        if ( match == null ) {
            return null;
        }
        Competition competition = match.getCompetition();
        if ( competition == null ) {
            return null;
        }
        UUID id = competition.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID matchHomeTeamId(Match match) {
        if ( match == null ) {
            return null;
        }
        Team homeTeam = match.getHomeTeam();
        if ( homeTeam == null ) {
            return null;
        }
        UUID id = homeTeam.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID matchAwayTeamId(Match match) {
        if ( match == null ) {
            return null;
        }
        Team awayTeam = match.getAwayTeam();
        if ( awayTeam == null ) {
            return null;
        }
        UUID id = awayTeam.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
