package com.football.boardgame.service;

import com.football.boardgame.domain.Manager;
import com.football.boardgame.dto.ManagerDTO;
import com.football.boardgame.mapper.ManagerMapper;
import com.football.boardgame.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;

    @Transactional(readOnly = true)
    public List<ManagerDTO> getAllManagers() {
        return managerRepository.findAll().stream()
                .map(managerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ManagerDTO getManagerById(UUID id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + id));
        return managerMapper.toDto(manager);
    }

    @Transactional
    @org.springframework.lang.NonNull
    public ManagerDTO createManager(ManagerDTO managerDTO) {
        Manager manager = managerMapper.toEntity(managerDTO);
        Manager savedManager = managerRepository.save(manager);
        ManagerDTO result = managerMapper.toDto(savedManager);
        if (result == null) {
            throw new RuntimeException("Error mapping saved manager to DTO");
        }
        return result;
    }
}
