package com.football.boardgame.config;

import com.football.boardgame.domain.GameEdition;
import com.football.boardgame.repository.GameEditionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final GameEditionRepository gameEditionRepository;

    @Override
    public void run(String... args) {
        if (gameEditionRepository.count() == 0) {
            log.info("No game editions found. Seeding default edition...");
            GameEdition defaultEdition = GameEdition.builder()
                    .name("Edición de juego por defecto")
                    .description("Configuración inicial estándar para empezar a jugar")
                    .startingBudget(300.0)
                    .build();
            gameEditionRepository.save(defaultEdition);
            log.info("Default game edition created.");
        }
    }
}
