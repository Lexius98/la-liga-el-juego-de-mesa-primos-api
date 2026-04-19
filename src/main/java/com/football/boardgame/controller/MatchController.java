package com.football.boardgame.controller;

import com.football.boardgame.dto.GoalRequestDTO;
import com.football.boardgame.dto.MatchDTO;
import com.football.boardgame.dto.MatchEventDTO;
import com.football.boardgame.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // ── Consultas ──────────────────────────────────────────────────────────────

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

    // ── Ciclo de vida (admin) ──────────────────────────────────────────────────

    /**
     * Admin actualiza el marcador en curso (sin finalizar).
     * PATCH /api/matches/{id}/score  body: { homeScore, awayScore }
     */
    @PatchMapping("/{id}/score")
    public ResponseEntity<MatchDTO> updateScore(
            @PathVariable("id") UUID id,
            @RequestBody Map<String, Integer> body) {
        int homeScore = body.getOrDefault("homeScore", 0);
        int awayScore = body.getOrDefault("awayScore", 0);
        return ResponseEntity.ok(matchService.updateScore(id, homeScore, awayScore));
    }

    /**
     * Admin finaliza el partido. Recalcula standings automáticamente.
     * PATCH /api/matches/{id}/finish
     */
    @PatchMapping("/{id}/finish")
    public ResponseEntity<MatchDTO> finishMatch(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(matchService.finishMatch(id));
    }

    /**
     * Retrocompatibilidad: actualiza resultado completo.
     * PUT /api/matches/{id}/result
     */
    @PutMapping("/{id}/result")
    public ResponseEntity<MatchDTO> updateResult(@PathVariable("id") UUID id, @RequestBody MatchDTO matchDTO) {
        return ResponseEntity.ok(matchService.updateMatchResult(id, matchDTO));
    }

    // ── Eventos ───────────────────────────────────────────────────────────────

    @PostMapping("/{id}/events")
    public ResponseEntity<MatchEventDTO> addEvent(@PathVariable("id") UUID id, @RequestBody MatchEventDTO eventDTO) {
        return ResponseEntity.ok(matchService.addMatchEvent(id, eventDTO));
    }

    // ── Goal Requests (manager → admin) ──────────────────────────────────────

    /**
     * Manager solicita validación de un gol.
     * POST /api/matches/{id}/goal-requests
     */
    @PostMapping("/{id}/goal-requests")
    public ResponseEntity<GoalRequestDTO> createGoalRequest(
            @PathVariable("id") UUID id,
            @RequestBody GoalRequestDTO requestDTO) {
        return ResponseEntity.ok(matchService.createGoalRequest(id, requestDTO));
    }

    /**
     * Admin lista las solicitudes pendientes de un partido.
     * GET /api/matches/{id}/goal-requests
     */
    @GetMapping("/{id}/goal-requests")
    public ResponseEntity<List<GoalRequestDTO>> getPendingGoalRequests(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(matchService.getPendingGoalRequests(id));
    }

    /**
     * Admin aprueba una solicitud de gol → crea el evento y actualiza marcador.
     * PATCH /api/matches/{id}/goal-requests/{reqId}/approve
     */
    @PatchMapping("/{id}/goal-requests/{reqId}/approve")
    public ResponseEntity<GoalRequestDTO> approveGoalRequest(
            @PathVariable("id") UUID id,
            @PathVariable("reqId") UUID reqId) {
        return ResponseEntity.ok(matchService.approveGoalRequest(id, reqId));
    }

    /**
     * Admin rechaza una solicitud de gol.
     * PATCH /api/matches/{id}/goal-requests/{reqId}/reject
     */
    @PatchMapping("/{id}/goal-requests/{reqId}/reject")
    public ResponseEntity<GoalRequestDTO> rejectGoalRequest(
            @PathVariable("id") UUID id,
            @PathVariable("reqId") UUID reqId) {
        return ResponseEntity.ok(matchService.rejectGoalRequest(id, reqId));
    }
}
