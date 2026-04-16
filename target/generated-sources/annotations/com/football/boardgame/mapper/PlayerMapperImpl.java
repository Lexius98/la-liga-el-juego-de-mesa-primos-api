package com.football.boardgame.mapper;

import com.football.boardgame.domain.Player;
import com.football.boardgame.domain.Team;
import com.football.boardgame.dto.PlayerDTO;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T10:00:05+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PlayerMapperImpl implements PlayerMapper {

    @Override
    public PlayerDTO toDto(Player player) {
        if ( player == null ) {
            return null;
        }

        PlayerDTO playerDTO = new PlayerDTO();

        playerDTO.setTeamId( playerTeamId( player ) );
        playerDTO.setAssistsMade( player.getAssistsMade() );
        playerDTO.setGoalsScored( player.getGoalsScored() );
        playerDTO.setId( player.getId() );
        playerDTO.setName( player.getName() );
        playerDTO.setPosition( player.getPosition() );

        return playerDTO;
    }

    @Override
    public Player toEntity(PlayerDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Player player = new Player();

        player.setId( dto.getId() );
        player.setAssistsMade( dto.getAssistsMade() );
        player.setGoalsScored( dto.getGoalsScored() );
        player.setName( dto.getName() );
        player.setPosition( dto.getPosition() );

        return player;
    }

    private UUID playerTeamId(Player player) {
        if ( player == null ) {
            return null;
        }
        Team team = player.getTeam();
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
