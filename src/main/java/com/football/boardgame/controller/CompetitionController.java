package com.football.boardgame.controller;

import com.football.boardgame.dto.CompetitionDTO;
import com.football.boardgame.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping
    public ResponseEntity<List<CompetitionDTO>> getCompetitionsBySeason(@RequestParam("seasonId") UUID seasonId) {
        return ResponseEntity.ok(competitionService.getCompetitionsBySeason(seasonId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompetitionDTO> getCompetition(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(competitionService.getCompetition(id));
    }

    @PostMapping
    public ResponseEntity<CompetitionDTO> createCompetition(@RequestBody CompetitionDTO competitionDTO) {
        return ResponseEntity.ok(competitionService.createCompetition(competitionDTO));
    }
}
