package com.football.boardgame.mapper;

import com.football.boardgame.domain.Match;
import com.football.boardgame.domain.MatchEvent;
import com.football.boardgame.domain.Player;
import com.football.boardgame.dto.MatchEventDTO;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T10:00:04+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MatchEventMapperImpl implements MatchEventMapper {

    @Override
    public MatchEventDTO toDto(MatchEvent event) {
        if ( event == null ) {
            return null;
        }

        MatchEventDTO matchEventDTO = new MatchEventDTO();

        matchEventDTO.setMatchId( eventMatchId( event ) );
        matchEventDTO.setPlayerId( eventPlayerId( event ) );
        matchEventDTO.setAssistPlayerId( eventAssistPlayerId( event ) );
        matchEventDTO.setId( event.getId() );
        matchEventDTO.setMinute( event.getMinute() );
        if ( event.getType() != null ) {
            matchEventDTO.setType( event.getType().name() );
        }

        return matchEventDTO;
    }

    @Override
    public MatchEvent toEntity(MatchEventDTO dto) {
        if ( dto == null ) {
            return null;
        }

        MatchEvent matchEvent = new MatchEvent();

        matchEvent.setId( dto.getId() );
        matchEvent.setMinute( dto.getMinute() );
        if ( dto.getType() != null ) {
            matchEvent.setType( Enum.valueOf( MatchEvent.MatchEventType.class, dto.getType() ) );
        }

        return matchEvent;
    }

    private UUID eventMatchId(MatchEvent matchEvent) {
        if ( matchEvent == null ) {
            return null;
        }
        Match match = matchEvent.getMatch();
        if ( match == null ) {
            return null;
        }
        UUID id = match.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID eventPlayerId(MatchEvent matchEvent) {
        if ( matchEvent == null ) {
            return null;
        }
        Player player = matchEvent.getPlayer();
        if ( player == null ) {
            return null;
        }
        UUID id = player.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID eventAssistPlayerId(MatchEvent matchEvent) {
        if ( matchEvent == null ) {
            return null;
        }
        Player assistPlayer = matchEvent.getAssistPlayer();
        if ( assistPlayer == null ) {
            return null;
        }
        UUID id = assistPlayer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
