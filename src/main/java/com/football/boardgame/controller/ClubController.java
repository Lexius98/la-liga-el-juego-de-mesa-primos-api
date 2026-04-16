package com.football.boardgame.controller;

import com.football.boardgame.dto.ClubDTO;
import com.football.boardgame.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    // We store logos relative to local run dir or docker volume
    private static final String LOGOS_DIR = "data/assets/logos/clubs";

    @GetMapping
    public ResponseEntity<List<ClubDTO>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubDTO> getClub(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(clubService.getClub(id));
    }

    @PostMapping
    public ResponseEntity<ClubDTO> createClub(@RequestBody ClubDTO dto) {
        return ResponseEntity.ok(clubService.createClub(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubDTO> updateClub(@PathVariable("id") UUID id, @RequestBody ClubDTO dto) {
        return ResponseEntity.ok(clubService.updateClub(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable("id") UUID id) {
        clubService.deleteClub(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/logo")
    public ResponseEntity<ClubDTO> uploadLogo(@PathVariable("id") UUID id, @RequestParam("file") MultipartFile file) {
        try {
            ClubDTO club = clubService.getClub(id);
            
            // Ensure dir exists
            Path uploadPath = Paths.get(LOGOS_DIR);
            Files.createDirectories(uploadPath);

            // Clean filename and handle extension
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            if (originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // Save as id + extension or standard name
            String filename = club.getShortName() != null ? 
                              club.getShortName().toLowerCase().replaceAll("[^a-z0-9]", "") + extension : 
                              id.toString() + extension;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update Club with relative path (assuming client prepends host)
            String localAssetUrl = "/logos/clubs/" + filename;
            club.setLogo(localAssetUrl);
            
            return ResponseEntity.ok(clubService.updateClub(id, club));
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
