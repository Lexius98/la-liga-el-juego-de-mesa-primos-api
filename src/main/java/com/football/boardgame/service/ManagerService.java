package com.football.boardgame.service;

import com.football.boardgame.domain.Manager;
import com.football.boardgame.dto.ManagerDTO;
import com.football.boardgame.mapper.ManagerMapper;
import com.football.boardgame.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    /** Sustituye todos los roles del manager por la lista indicada. */
    @Transactional
    public ManagerDTO setRoles(UUID managerId, Set<String> roleNames) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + managerId));
        Set<Manager.ManagerRole> parsed = roleNames.stream()
                .map(r -> Manager.ManagerRole.valueOf(r.toUpperCase()))
                .collect(java.util.stream.Collectors.toSet());
        if (parsed.isEmpty()) parsed.add(Manager.ManagerRole.PLAYER); // siempre al menos PLAYER
        manager.setRoles(parsed);
        return managerMapper.toDto(managerRepository.save(manager));
    }

    /** Añade un rol al manager (sin quitar los que ya tiene). */
    @Transactional
    public ManagerDTO addRole(UUID managerId, String role) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + managerId));
        manager.getRoles().add(Manager.ManagerRole.valueOf(role.toUpperCase()));
        return managerMapper.toDto(managerRepository.save(manager));
    }

    /** Quita un rol del manager (siempre queda al menos PLAYER). */
    @Transactional
    public ManagerDTO removeRole(UUID managerId, String role) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + managerId));
        manager.getRoles().remove(Manager.ManagerRole.valueOf(role.toUpperCase()));
        if (manager.getRoles().isEmpty()) manager.getRoles().add(Manager.ManagerRole.PLAYER);
        return managerMapper.toDto(managerRepository.save(manager));
    }
}
