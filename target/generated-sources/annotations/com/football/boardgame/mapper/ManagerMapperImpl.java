package com.football.boardgame.mapper;

import com.football.boardgame.domain.Manager;
import com.football.boardgame.dto.ManagerDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T10:00:05+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ManagerMapperImpl implements ManagerMapper {

    @Override
    public ManagerDTO toDto(Manager manager) {
        if ( manager == null ) {
            return null;
        }

        ManagerDTO.ManagerDTOBuilder managerDTO = ManagerDTO.builder();

        managerDTO.global_stats( managerToGlobalStatsDTO( manager ) );
        managerDTO.avatarUrl( manager.getAvatarUrl() );
        managerDTO.email( manager.getEmail() );
        managerDTO.googleId( manager.getGoogleId() );
        managerDTO.id( manager.getId() );
        managerDTO.name( manager.getName() );

        return managerDTO.build();
    }

    @Override
    public Manager toEntity(ManagerDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Manager.ManagerBuilder<?, ?> manager = Manager.builder();

        manager.matchesPlayed( dtoGlobal_statsMatches_played( dto ) );
        manager.competitionsWon( dtoGlobal_statsCompetitions_won( dto ) );
        manager.id( dto.getId() );
        manager.avatarUrl( dto.getAvatarUrl() );
        manager.email( dto.getEmail() );
        manager.googleId( dto.getGoogleId() );
        manager.name( dto.getName() );

        return manager.build();
    }

    protected ManagerDTO.GlobalStatsDTO managerToGlobalStatsDTO(Manager manager) {
        if ( manager == null ) {
            return null;
        }

        ManagerDTO.GlobalStatsDTO.GlobalStatsDTOBuilder globalStatsDTO = ManagerDTO.GlobalStatsDTO.builder();

        globalStatsDTO.matches_played( manager.getMatchesPlayed() );
        globalStatsDTO.competitions_won( manager.getCompetitionsWon() );

        return globalStatsDTO.build();
    }

    private Integer dtoGlobal_statsMatches_played(ManagerDTO managerDTO) {
        if ( managerDTO == null ) {
            return null;
        }
        ManagerDTO.GlobalStatsDTO global_stats = managerDTO.getGlobal_stats();
        if ( global_stats == null ) {
            return null;
        }
        Integer matches_played = global_stats.getMatches_played();
        if ( matches_played == null ) {
            return null;
        }
        return matches_played;
    }

    private Integer dtoGlobal_statsCompetitions_won(ManagerDTO managerDTO) {
        if ( managerDTO == null ) {
            return null;
        }
        ManagerDTO.GlobalStatsDTO global_stats = managerDTO.getGlobal_stats();
        if ( global_stats == null ) {
            return null;
        }
        Integer competitions_won = global_stats.getCompetitions_won();
        if ( competitions_won == null ) {
            return null;
        }
        return competitions_won;
    }
}
