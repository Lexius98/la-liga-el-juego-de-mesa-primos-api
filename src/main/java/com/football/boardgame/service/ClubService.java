package com.football.boardgame.service;

import com.football.boardgame.domain.Club;
import com.football.boardgame.dto.ClubDTO;
import com.football.boardgame.mapper.ClubMapper;
import com.football.boardgame.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubMapper clubMapper;

    @Transactional(readOnly = true)
    public List<ClubDTO> getAllClubs() {
        return clubRepository.findAll().stream()
                .map(clubMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClubDTO getClub(UUID id) {
        return clubRepository.findById(id)
                .map(clubMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Club not found"));
    }

    @Transactional
    public ClubDTO createClub(ClubDTO dto) {
        Club club = clubMapper.toEntity(dto);
        return clubMapper.toDto(clubRepository.save(club));
    }

    @Transactional
    public ClubDTO updateClub(UUID id, ClubDTO dto) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        
        club.setName(dto.getName());
        club.setShortName(dto.getShortName());
        club.setDescription(dto.getDescription());
        club.setLocation(dto.getLocation());
        club.setPrimaryColor(dto.getPrimaryColor());
        club.setSecondaryColor(dto.getSecondaryColor());
        club.setCompetitionTags(dto.getCompetitionTags());
        
        if (dto.getLogo() != null) {
            club.setLogo(dto.getLogo());
        }

        return clubMapper.toDto(clubRepository.save(club));
    }

    @Transactional
    public void deleteClub(UUID id) {
        clubRepository.deleteById(id);
    }
}
