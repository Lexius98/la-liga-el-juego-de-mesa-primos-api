package com.football.boardgame.controller;

import com.football.boardgame.dto.SeasonDTO;
import com.football.boardgame.service.SeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seasons")
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonService seasonService;

    @GetMapping
    public ResponseEntity<List<SeasonDTO>> getAllSeasons() {
        return ResponseEntity.ok(seasonService.getAllSeasons());
    }

    @PostMapping
    public ResponseEntity<SeasonDTO> createSeason(@RequestBody SeasonDTO seasonDTO) {
        return ResponseEntity.ok(seasonService.createSeason(seasonDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeasonDTO> getSeasonById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(seasonService.getSeasonById(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<SeasonDTO> updateSeason(@PathVariable("id") UUID id, @RequestBody SeasonDTO seasonDTO) {
        return ResponseEntity.ok(seasonService.updateSeason(id, seasonDTO));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeason(@PathVariable("id") UUID id) {
        seasonService.deleteSeason(id);
        return ResponseEntity.noContent().build();
    }
}
