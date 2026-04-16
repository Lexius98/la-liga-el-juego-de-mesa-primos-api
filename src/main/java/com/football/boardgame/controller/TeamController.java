package com.football.boardgame.controller;

import com.football.boardgame.dto.TeamDTO;
import com.football.boardgame.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getTeams(@RequestParam("seasonId") UUID seasonId) {
        return ResponseEntity.ok(teamService.getTeamsBySeason(seasonId));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> createOrUpdateTeam(@RequestBody TeamDTO teamDTO) {
        return ResponseEntity.ok(teamService.createOrUpdateTeam(teamDTO));
    }
}
