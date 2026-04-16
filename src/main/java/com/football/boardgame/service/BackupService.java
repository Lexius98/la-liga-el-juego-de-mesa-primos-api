package com.football.boardgame.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.football.boardgame.dto.*;
import com.football.boardgame.mapper.*;
import com.football.boardgame.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final SeasonRepository seasonRepository;
    private final CompetitionRepository competitionRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final StandingsRepository standingsRepository;
    private final ManagerRepository managerRepository;
    private final SeasonMembershipRepository seasonMembershipRepository;

    private final SeasonMapper seasonMapper;
    private final CompetitionMapper competitionMapper;
    private final TeamMapper teamMapper;
    private final MatchMapper matchMapper;
    private final MatchEventMapper matchEventMapper;
    private final StandingsMapper standingsMapper;
    private final ManagerMapper managerMapper;
    private final SeasonMembershipMapper membershipMapper;

    private final GoogleDriveService googleDriveService;

    private static final String BACKUP_DIR = "D:/FootballBoardGame/data/backups";
    private static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ofPattern("'backup_'yyyy_MM_dd_HH_mm");

    // pg_dump path (native PostgreSQL 18 installation)
    private static final String PG_DUMP_PATH = "C:/Program Files/PostgreSQL/18/bin/pg_dump.exe";
    private static final String DB_NAME = "football_db";
    private static final String DB_USER = "postgres";

    // ─── JSON BACKUP (legacy full DTO export) ───────────────────────────────

    @Transactional(readOnly = true)
    public String createJsonBackup() throws IOException {
        Map<String, Object> backup = new LinkedHashMap<>();
        backup.put("version", "1.0");
        backup.put("createdAt", LocalDateTime.now().toString());
        backup.put("seasons", seasonRepository.findAll().stream().map(seasonMapper::toDto).collect(Collectors.toList()));
        backup.put("competitions", competitionRepository.findAll().stream().map(competitionMapper::toDto).collect(Collectors.toList()));
        backup.put("managers", managerRepository.findAll().stream().map(managerMapper::toDto).collect(Collectors.toList()));
        backup.put("teams", teamRepository.findAll().stream().map(teamMapper::toDto).collect(Collectors.toList()));
        backup.put("memberships", seasonMembershipRepository.findAll().stream().map(membershipMapper::toDto).collect(Collectors.toList()));
        backup.put("matches", matchRepository.findAll().stream().map(matchMapper::toDto).collect(Collectors.toList()));
        backup.put("matchEvents", matchEventRepository.findAll().stream().map(matchEventMapper::toDto).collect(Collectors.toList()));
        backup.put("standings", standingsRepository.findAll().stream().map(standingsMapper::toDto).collect(Collectors.toList()));

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);

        Path backupDir = Paths.get(BACKUP_DIR);
        Files.createDirectories(backupDir);

        String filename = LocalDateTime.now().format(FILE_FORMATTER) + ".json";
        Path filePath = backupDir.resolve(filename);
        mapper.writeValue(filePath.toFile(), backup);

        log.info("JSON backup creado: {}", filePath.toAbsolutePath());
        return filename;
    }

    // Keep old name as alias for controllers that still use it
    @Transactional(readOnly = true)
    public String createBackup() throws IOException {
        return createJsonBackup();
    }

    // ─── SQL BACKUP via pg_dump ──────────────────────────────────────────────

    /**
     * Runs pg_dump to generate a .sql file of the entire football_db database.
     * The file is saved to D:/FootballBoardGame/data/backups/
     *
     * @return the filename of the generated SQL file
     */
    public String createSqlBackup() throws IOException, InterruptedException {
        Path backupDir = Paths.get(BACKUP_DIR);
        Files.createDirectories(backupDir);

        String filename = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("'backup_'yyyy_MM_dd_HH_mm'.sql'"));
        Path outputFile = backupDir.resolve(filename);

        ProcessBuilder pb = new ProcessBuilder(
                PG_DUMP_PATH,
                "-U", DB_USER,
                "-d", DB_NAME,
                "-F", "p",           // plain SQL format
                "-f", outputFile.toString()
        );
        pb.environment().put("PGPASSWORD", ""); // empty password
        pb.redirectErrorStream(true);

        log.info("Ejecutando pg_dump -> {}", outputFile);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            String output = new String(process.getInputStream().readAllBytes());
            throw new IOException("pg_dump falló (exit " + exitCode + "): " + output);
        }

        log.info("SQL backup creado: {}", outputFile.toAbsolutePath());
        return filename;
    }

    // ─── GOOGLE DRIVE UPLOAD ─────────────────────────────────────────────────

    /**
     * Creates a SQL backup via pg_dump and uploads it to Google Drive.
     *
     * @return Map with filename and driveFileId
     */
    public Map<String, String> createAndUploadBackup() throws IOException, InterruptedException, GeneralSecurityException {
        String filename = createSqlBackup();
        Path filePath = Paths.get(BACKUP_DIR).resolve(filename);

        String driveFileId = googleDriveService.uploadBackup(filePath, "application/sql");
        log.info("Backup '{}' subido a Drive con id: {}", filename, driveFileId);

        return Map.of(
                "filename", filename,
                "driveFileId", driveFileId,
                "status", "ok"
        );
    }

    // ─── LIST BACKUPS ────────────────────────────────────────────────────────

    public List<Map<String, Object>> listBackups() {
        Path backupDir = Paths.get(BACKUP_DIR);
        List<Map<String, Object>> result = new ArrayList<>();
        if (!Files.exists(backupDir)) return result;

        try {
            Files.list(backupDir)
                    .filter(p -> p.toString().endsWith(".json") || p.toString().endsWith(".sql"))
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        Map<String, Object> entry = new LinkedHashMap<>();
                        entry.put("filename", p.getFileName().toString());
                        try {
                            entry.put("sizeKb", Files.size(p) / 1024);
                            entry.put("lastModified", Files.getLastModifiedTime(p).toString());
                        } catch (IOException ignored) {}
                        result.add(entry);
                    });
        } catch (IOException e) {
            log.error("Error listando backups", e);
        }
        return result;
    }
}
