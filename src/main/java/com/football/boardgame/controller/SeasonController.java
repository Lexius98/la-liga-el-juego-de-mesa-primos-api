package com.football.boardgame.controller;

import com.football.boardgame.dto.MatchDTO;
import com.football.boardgame.dto.RoundAdvanceDTO;
import com.football.boardgame.dto.SeasonDTO;
import com.football.boardgame.service.MatchSimulatorService;
import com.football.boardgame.service.SeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final MatchSimulatorService matchSimulatorService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeason(@PathVariable("id") UUID id) {
        seasonService.deleteSeason(id);
        return ResponseEntity.noContent().build();
    }

    // ── Fixture endpoints (Issue #7) ──────────────────────────────────────────

    /**
     * Genera el fixture de liga (ida + vuelta) al finalizar la pretemporada.
     * La temporada debe estar en estado ACTIVE.
     * POST /api/seasons/{id}/generate-fixture
     */
    @PostMapping("/{id}/generate-fixture")
    public ResponseEntity<List<MatchDTO>> generateFixture(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(seasonService.generateFixture(id));
    }

    /**
     * Devuelve el fixture completo ordenado por jornada.
     * GET /api/seasons/{id}/fixture
     */
    @GetMapping("/{id}/fixture")
    public ResponseEntity<List<MatchDTO>> getFixture(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(seasonService.getFixture(id));
    }

    /**
     * Devuelve los partidos de una jornada específica.
     * GET /api/seasons/{id}/fixture/round/{round}
     */
    @GetMapping("/{id}/fixture/round/{round}")
    public ResponseEntity<List<MatchDTO>> getFixtureByRound(
            @PathVariable("id") UUID id,
            @PathVariable("round") int round) {
        return ResponseEntity.ok(seasonService.getFixtureByRound(id, round));
    }

    /**
     * Simula todos los partidos NPC de una jornada (al final de la misma).
     * Solo simula partidos matchType=NPC y status=SCHEDULED.
     * POST /api/seasons/{id}/simulate-round/{round}
     */
    @PostMapping("/{id}/simulate-round/{round}")
    public ResponseEntity<List<MatchDTO>> simulateRound(
            @PathVariable("id") UUID id,
            @PathVariable("round") int round) {
        return ResponseEntity.ok(matchSimulatorService.simulateRound(id, round));
    }

    // ── B-017: Avance de jornada manual ──────────────────────────────

    /**
     * El host cierra la jornada y activa la siguiente.
     * Simula los NPC pendientes, recalcula standings y notifica va STOMP.
     * POST /api/seasons/{id}/matchdays/{round}/advance
     */
    @PostMapping("/{id}/matchdays/{round}/advance")
    public ResponseEntity<RoundAdvanceDTO> advanceRound(
            @PathVariable("id") UUID id,
            @PathVariable("round") int round) {
        return ResponseEntity.ok(seasonService.advanceRound(id, round));
    }
}
