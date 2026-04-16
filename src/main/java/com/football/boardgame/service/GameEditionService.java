package com.football.boardgame.service;

import com.football.boardgame.domain.GameEdition;
import com.football.boardgame.repository.GameEditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GameEditionService {

    private final GameEditionRepository repository;

    public List<GameEdition> getAll() {
        return repository.findAll();
    }

    public GameEdition getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Edition not found"));
    }

    public GameEdition create(GameEdition edition) {
        return repository.save(edition);
    }

    public GameEdition update(UUID id, GameEdition edition) {
        GameEdition existing = getById(id);
        existing.setName(edition.getName());
        existing.setDescription(edition.getDescription());
        existing.setStartingBudget(edition.getStartingBudget());
        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
