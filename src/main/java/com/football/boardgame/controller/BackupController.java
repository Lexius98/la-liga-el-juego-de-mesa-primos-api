package com.football.boardgame.controller;

import com.football.boardgame.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    /**
     * POST /api/admin/backup
     * Genera un backup JSON de la BD y devuelve el nombre del archivo.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createBackup() {
        try {
            String filename = backupService.createBackup();
            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "filename", filename,
                    "message", "Backup JSON creado correctamente"
            ));
        } catch (IOException e) {
            log.error("Error al crear backup JSON", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Error al crear el backup: " + e.getMessage()
            ));
        }
    }

    /**
     * POST /api/admin/backup/sql
     * Genera un backup via pg_dump (.sql) en D:/FootballBoardGame/data/backups/.
     */
    @PostMapping("/sql")
    public ResponseEntity<Map<String, String>> createSqlBackup() {
        try {
            String filename = backupService.createSqlBackup();
            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "filename", filename,
                    "message", "Backup SQL creado correctamente en D:/FootballBoardGame/data/backups/"
            ));
        } catch (Exception e) {
            log.error("Error al crear backup SQL", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Error al crear backup SQL: " + e.getMessage()
            ));
        }
    }

    /**
     * POST /api/admin/backup/drive
     * Genera un backup SQL via pg_dump y lo sube automáticamente a Google Drive.
     * En la primera llamada abrirá el navegador para autorización OAuth.
     */
    @PostMapping("/drive")
    public ResponseEntity<Map<String, String>> createAndUploadBackup() {
        try {
            Map<String, String> result = backupService.createAndUploadBackup();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error al subir backup a Drive", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Error al subir a Drive: " + e.getMessage()
            ));
        }
    }

    /**
     * GET /api/admin/backup
     * Lista todos los archivos de backup disponibles localmente.
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listBackups() {
        return ResponseEntity.ok(backupService.listBackups());
    }
}
