package com.football.boardgame.controller;

import com.football.boardgame.dto.StandingsDTO;
import com.football.boardgame.service.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/standings")
@RequiredArgsConstructor
public class StandingController {

    private final StandingService standingService;

    @GetMapping
    public ResponseEntity<List<StandingsDTO>> getStandings(@RequestParam("competitionId") UUID competitionId) {
        return ResponseEntity.ok(standingService.getStandingsByCompetition(competitionId));
    }
}
