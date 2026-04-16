package com.football.boardgame.mapper;

import com.football.boardgame.domain.Competition;
import com.football.boardgame.domain.Standings;
import com.football.boardgame.domain.Team;
import com.football.boardgame.dto.StandingsDTO;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T10:00:04+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class StandingsMapperImpl implements StandingsMapper {

    @Override
    public StandingsDTO toDto(Standings standings) {
        if ( standings == null ) {
            return null;
        }

        StandingsDTO standingsDTO = new StandingsDTO();

        standingsDTO.setCompetitionId( standingsCompetitionId( standings ) );
        standingsDTO.setTeamId( standingsTeamId( standings ) );
        standingsDTO.setMatches_played( standings.getMatchesPlayed() );
        standingsDTO.setGoals_for( standings.getGoalsFor() );
        standingsDTO.setGoals_against( standings.getGoalsAgainst() );
        standingsDTO.setGoal_difference( standings.getGoalDifference() );
        standingsDTO.setDraws( standings.getDraws() );
        standingsDTO.setId( standings.getId() );
        standingsDTO.setLosses( standings.getLosses() );
        standingsDTO.setPoints( standings.getPoints() );
        standingsDTO.setPosition( standings.getPosition() );
        standingsDTO.setWins( standings.getWins() );

        return standingsDTO;
    }

    @Override
    public Standings toEntity(StandingsDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Standings standings = new Standings();

        standings.setMatchesPlayed( dto.getMatches_played() );
        standings.setGoalsFor( dto.getGoals_for() );
        standings.setGoalsAgainst( dto.getGoals_against() );
        standings.setGoalDifference( dto.getGoal_difference() );
        standings.setId( dto.getId() );
        standings.setDraws( dto.getDraws() );
        standings.setLosses( dto.getLosses() );
        standings.setPoints( dto.getPoints() );
        standings.setPosition( dto.getPosition() );
        standings.setWins( dto.getWins() );

        return standings;
    }

    private UUID standingsCompetitionId(Standings standings) {
        if ( standings == null ) {
            return null;
        }
        Competition competition = standings.getCompetition();
        if ( competition == null ) {
            return null;
        }
        UUID id = competition.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID standingsTeamId(Standings standings) {
        if ( standings == null ) {
            return null;
        }
        Team team = standings.getTeam();
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
