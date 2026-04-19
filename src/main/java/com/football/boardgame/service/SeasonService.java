package com.football.boardgame.service;

import com.football.boardgame.domain.Competition;
import com.football.boardgame.domain.Season;
import com.football.boardgame.domain.SeasonMembership;
import com.football.boardgame.domain.Standings;
import com.football.boardgame.domain.Team;
import com.football.boardgame.dto.MatchDTO;
import com.football.boardgame.dto.SeasonDTO;
import com.football.boardgame.mapper.MatchMapper;
import com.football.boardgame.mapper.SeasonMapper;
import com.football.boardgame.repository.CompetitionRepository;
import com.football.boardgame.repository.GameEditionRepository;
import com.football.boardgame.repository.ManagerRepository;
import com.football.boardgame.repository.MatchRepository;
import com.football.boardgame.repository.SeasonMembershipRepository;
import com.football.boardgame.repository.SeasonRepository;
import com.football.boardgame.repository.StandingsRepository;
import com.football.boardgame.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
// Hot-reload verification comment
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final SeasonMapper seasonMapper;
    private final SeasonMembershipRepository membershipRepository;
    private final ManagerRepository managerRepository;
    private final TeamRepository teamRepository;
    private final CompetitionRepository competitionRepository;
    private final StandingsRepository standingsRepository;
    private final GameEditionRepository gameEditionRepository;
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final FixtureGeneratorService fixtureGenerator;

    @Transactional(readOnly = true)
    public List<SeasonDTO> getAllSeasons() {
        return seasonRepository.findAll().stream()
                .map(seasonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @org.springframework.lang.NonNull
    public SeasonDTO createSeason(SeasonDTO seasonDTO) {
        log.info("Creating new season: {}", seasonDTO.getName());
        Season season = seasonMapper.toEntity(seasonDTO);

        // Load Game Edition if provided
        if (seasonDTO.getGame_version_id() != null) {
            season.setGameEdition(gameEditionRepository.findById(seasonDTO.getGame_version_id())
                    .orElseThrow(() -> new RuntimeException("Game Edition not found: " + seasonDTO.getGame_version_id())));
        }
        
        // Default status if not provided
        if (seasonDTO.getStatus() != null) {
            season.setStatus(Season.SeasonStatus.valueOf(seasonDTO.getStatus()));
        } else {
            season.setStatus(Season.SeasonStatus.DRAFT);
        }
        
        // Generate a lobby code if it's LOBBY status (or always for future use)
        // For now, let's just use a simple random 6-char code if status is DRAFT/LOBBY
        if (season.getStatus() == Season.SeasonStatus.DRAFT || season.getStatus() == Season.SeasonStatus.LOBBY) {
            // Placeholder: implement lobby code generation logic if needed
        }

        Season savedSeason = seasonRepository.save(season);

        // 1. Initialize Default Competition (League)
        Competition league = Competition.builder()
                .season(savedSeason)
                .name("Liga " + savedSeason.getName())
                .type(Competition.CompetitionType.LEAGUE)
                .status(Competition.CompetitionStatus.UPCOMING)
                .build();
        Competition savedLeague = competitionRepository.save(league);

        // 2. Handle Participants (SeasonMemberships) and initial Standings
        if (seasonDTO.getParticipants() != null) {
            for (SeasonDTO.ParticipantDTO part : seasonDTO.getParticipants()) {
                UUID managerId = part.getManager_id();
                if (managerId != null) {
                    SeasonMembership membership = SeasonMembership.builder()
                            .season(savedSeason)
                            .manager(managerRepository.findById(managerId)
                                    .orElseThrow(() -> new RuntimeException("Manager not found: " + managerId)))
                            .team(part.getTeam_id() != null ? teamRepository.findById(part.getTeam_id()).orElse(null) : null)
                            .status(SeasonMembership.MembershipStatus.JOINED)
                            .joinedAt(LocalDateTime.now())
                            .build();
                    
                    membershipRepository.save(membership);
                    savedSeason.getMemberships().add(membership);

                    // Initialize Standings for this team in the league
                    if (membership.getTeam() != null) {
                        Standings standings = Standings.builder()
                                .competition(savedLeague)
                                .team(membership.getTeam())
                                .points(0)
                                .matchesPlayed(0)
                                .wins(0)
                                .draws(0)
                                .losses(0)
                                .goalsFor(0)
                                .goalsAgainst(0)
                                .goalDifference(0)
                                .build();
                        standingsRepository.save(standings);
                    }
                }
            }
        }

        SeasonDTO result = seasonMapper.toDto(savedSeason);
        if (result == null) {
            throw new RuntimeException("Error mapping saved season to DTO");
        }
        return result;
    }

    @Transactional(readOnly = true)
    @org.springframework.lang.NonNull
    public SeasonDTO getSeasonById(UUID id) {
        Season season = seasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Season not found with id: " + id));
        SeasonDTO dto = seasonMapper.toDto(season);
        if (dto == null) {
            throw new RuntimeException("Error mapping season to DTO");
        }
        return dto;
    }

    @Transactional
    public SeasonDTO updateSeason(UUID id, SeasonDTO seasonDTO) {
        Season existing = seasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Season not found with id: " + id));
        
        // Update fields if provided
        if (seasonDTO.getName() != null) existing.setName(seasonDTO.getName());
        if (seasonDTO.getStatus() != null) {
            existing.setStatus(Season.SeasonStatus.valueOf(seasonDTO.getStatus()));
        }

        if (seasonDTO.getGame_version_id() != null) {
            existing.setGameEdition(gameEditionRepository.findById(seasonDTO.getGame_version_id())
                    .orElseThrow(() -> new RuntimeException("Game Edition not found: " + seasonDTO.getGame_version_id())));
        }
        
        Season saved = seasonRepository.save(existing);
        return seasonMapper.toDto(saved);
    }

    @Transactional
    public void deleteSeason(UUID id) {
        seasonRepository.deleteById(id);
    }

    // ── Fixture (Issue #7) ────────────────────────────────────────────────────

    /**
     * Genera el fixture de liga Round-Robin (ida + vuelta) para la temporada.
     * Se llama al final de la pretemporada, cuando la temporada ya está ACTIVE.
     *
     * @throws IllegalStateException si el fixture ya existe o la temporada no está ACTIVE
     */
    @Transactional
    public List<MatchDTO> generateFixture(UUID seasonId) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new RuntimeException("Season not found: " + seasonId));

        if (season.getStatus() != Season.SeasonStatus.ACTIVE) {
            throw new IllegalStateException(
                "El fixture solo puede generarse con la temporada en estado ACTIVE. Estado actual: "
                + season.getStatus());
        }

        // Obtener la competición de liga
        Competition league = competitionRepository.findBySeasonId(seasonId).stream()
                .filter(c -> c.getType() == Competition.CompetitionType.LEAGUE)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Liga no encontrada para la temporada: " + seasonId));

        // Evitar doble generación
        if (matchRepository.existsByCompetitionId(league.getId())) {
            throw new IllegalStateException(
                "El fixture ya ha sido generado para esta temporada. Jornadas existentes: " +
                matchRepository.findByCompetitionId(league.getId()).size() + " partidos.");
        }

        // Equipos de los managers participantes
        List<Team> teams = membershipRepository.findBySeasonId(seasonId).stream()
                .map(SeasonMembership::getTeam)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Generar y persistir
        List<com.football.boardgame.domain.Match> matches = fixtureGenerator.generate(
                teams, league, season.getStartDate());
        matchRepository.saveAll(matches);

        // Persistir winterBreakAfterRound en la competition
        competitionRepository.save(league);

        log.info("[SeasonService] Fixture generado para temporada '{}': {} partidos en {} jornadas",
                season.getName(), matches.size(), 2 * (teams.size() - 1));

        return matches.stream().map(matchMapper::toDto).collect(Collectors.toList());
    }

    /** Devuelve el fixture completo de la temporada ordenado por jornada. */
    @Transactional(readOnly = true)
    public List<MatchDTO> getFixture(UUID seasonId) {
        return matchRepository.findFixtureBySeason(seasonId).stream()
                .map(matchMapper::toDto)
                .collect(Collectors.toList());
    }

    /** Devuelve los partidos de una jornada concreta. */
    @Transactional(readOnly = true)
    public List<MatchDTO> getFixtureByRound(UUID seasonId, int round) {
        return matchRepository.findFixtureBySeasonAndRound(seasonId, round).stream()
                .map(matchMapper::toDto)
                .collect(Collectors.toList());
    }
}
