package com.football.boardgame.controller;

import com.football.boardgame.domain.Manager;
import com.football.boardgame.dto.ManagerDTO;
import com.football.boardgame.mapper.ManagerMapper;
import com.football.boardgame.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;

    @GetMapping("/me")
    public ResponseEntity<ManagerDTO> authenticateGoogle(@AuthenticationPrincipal Jwt jwt) {
        String googleId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String picture = jwt.getClaimAsString("picture");

        Manager manager = managerRepository.findByGoogleId(googleId).orElseGet(() -> {
            Manager newManager = new Manager();
            newManager.setGoogleId(googleId);
            newManager.setEmail(email != null ? email : "no-email@google.com");
            newManager.setName(name != null ? name : "User " + googleId.substring(0, 5));
            newManager.setAvatarUrl(picture);
            return managerRepository.save(newManager);
        });

        // Update avatar if changed
        if (picture != null && !picture.equals(manager.getAvatarUrl())) {
            manager.setAvatarUrl(picture);
            managerRepository.save(manager);
        }

        return ResponseEntity.ok(managerMapper.toDto(manager));
    }
}
