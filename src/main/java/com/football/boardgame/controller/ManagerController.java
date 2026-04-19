package com.football.boardgame.controller;

import com.football.boardgame.dto.ManagerDTO;
import com.football.boardgame.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping
    public ResponseEntity<List<ManagerDTO>> getAllManagers() {
        return ResponseEntity.ok(managerService.getAllManagers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManagerDTO> getManagerById(@PathVariable UUID id) {
        return ResponseEntity.ok(managerService.getManagerById(id));
    }

    @PostMapping
    public ResponseEntity<ManagerDTO> createManager(@RequestBody ManagerDTO managerDTO) {
        return ResponseEntity.ok(managerService.createManager(managerDTO));
    }

    // ── Gestión de roles (multi-rol) ─────────────────────────────────────── 

    /**
     * Sustituye el conjunto completo de roles.
     * PUT /api/managers/{id}/roles
     * Body: { "roles": ["ADMIN", "SCANNER", "PLAYER"] }
     */
    @PutMapping("/{id}/roles")
    public ResponseEntity<ManagerDTO> setRoles(
            @PathVariable UUID id,
            @RequestBody Map<String, Set<String>> body) {
        Set<String> roles = body.get("roles");
        if (roles == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(managerService.setRoles(id, roles));
    }

    /**
     * Añade un rol al manager (sin quitar los existentes).
     * POST /api/managers/{id}/roles/{role}
     */
    @PostMapping("/{id}/roles/{role}")
    public ResponseEntity<ManagerDTO> addRole(
            @PathVariable UUID id,
            @PathVariable String role) {
        return ResponseEntity.ok(managerService.addRole(id, role));
    }

    /**
     * Quita un rol del manager.
     * DELETE /api/managers/{id}/roles/{role}
     */
    @DeleteMapping("/{id}/roles/{role}")
    public ResponseEntity<ManagerDTO> removeRole(
            @PathVariable UUID id,
            @PathVariable String role) {
        return ResponseEntity.ok(managerService.removeRole(id, role));
    }
}

