package com.football.boardgame.controller;

import com.football.boardgame.domain.GameEdition;
import com.football.boardgame.service.GameEditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/editions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameEditionController {

    private final GameEditionService service;

    @GetMapping
    public ResponseEntity<List<GameEdition>> getAllEditions() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameEdition> getEdition(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<GameEdition> createEdition(@RequestBody GameEdition edition) {
        return ResponseEntity.ok(service.create(edition));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameEdition> updateEdition(@PathVariable("id") UUID id, @RequestBody GameEdition edition) {
        return ResponseEntity.ok(service.update(id, edition));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEdition(@PathVariable("id") UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
