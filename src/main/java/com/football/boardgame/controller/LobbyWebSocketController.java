package com.football.boardgame.controller;

import com.football.boardgame.dto.ManagerDTO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class LobbyWebSocketController {

    /**
     * When a client publishes to /app/season/{id}/join with their ManagerDTO, 
     * this method receives it and broadcasts it to all subscribers of /topic/season/{id}/lobby.
     */
    @MessageMapping("/season/{id}/join")
    @SendTo("/topic/season/{id}/lobby")
    public ManagerDTO joinLobby(@DestinationVariable("id") UUID id, @Payload ManagerDTO manager) {
        // Here we could also persist the SeasonMembership in the DB 
        // to maintain the true state of the lobby before broadcasting.
        return manager;
    }

    /**
     * Similar setup for ready state.
     */
    @MessageMapping("/season/{id}/ready")
    @SendTo("/topic/season/{id}/lobby")
    public ManagerDTO readyLobby(@DestinationVariable("id") UUID id, @Payload ManagerDTO manager) {
        return manager;
    }
}
