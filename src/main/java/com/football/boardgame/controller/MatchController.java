package com.football.boardgame.controller;

import com.football.boardgame.dto.MatchDTO;
import com.football.boardgame.dto.MatchEventDTO;
import com.football.boardgame.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<MatchDTO>> getMatches(@RequestParam("competitionId") UUID competitionId) {
        return ResponseEntity.ok(matchService.getMatchesByCompetition(competitionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatch(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<MatchDTO> getActiveMatch(@RequestParam("seasonId") UUID seasonId) {
        MatchDTO active = matchService.getActiveMatchBySeason(seasonId);
        return active != null ? ResponseEntity.ok(active) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<MatchDTO> updateResult(@PathVariable("id") UUID id, @RequestBody MatchDTO matchDTO) {
        return ResponseEntity.ok(matchService.updateMatchResult(id, matchDTO));
    }

    @PostMapping("/{id}/events")
    public ResponseEntity<MatchEventDTO> addEvent(@PathVariable("id") UUID id, @RequestBody MatchEventDTO eventDTO) {
        return ResponseEntity.ok(matchService.addMatchEvent(id, eventDTO));
    }
}
