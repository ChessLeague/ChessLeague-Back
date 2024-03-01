package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.LeagueEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.TournamentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.LeagueRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.TournamentRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TournamentLeagueService {
    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Transactional
    public LeagueEntity addLeague(Long tournamentId, Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the tournament with ID {0} to the league with ID {1}.", tournamentId, leagueId);
        if (tournamentId == null || tournamentId == 0L || leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        tournamentEntity.get().setLeague(leagueEntity.get());
        log.info("Finish the process of associating the tournament with ID {0} to the league with ID {1}.", tournamentId, leagueId);
        return leagueEntity.get();
    }

    @Transactional
    public LeagueEntity getLeague(Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the league of the tournament with ID {0}", tournamentId);
        if (tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        LeagueEntity leagueEntity = tournamentEntity.get().getLeague();
        if (leagueEntity == null) {
            throw new EntityNotFoundException("The league of the tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        log.info("Finish the process of querying the league of the tournament with ID {0}", tournamentId);
        return leagueEntity;
    }

    @Transactional
    public TournamentEntity replaceLeague(Long tournamentId, Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the league with ID {0} for the tournament with ID {1}.", leagueId, tournamentId);
        if (tournamentId == null || tournamentId == 0L || leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        if (!leagueEntity.get().getTournaments().contains(tournamentEntity.get())) {
            leagueEntity.get().getTournaments().add(tournamentEntity.get());
        }
        tournamentEntity.get().setLeague(leagueEntity.get());
        log.info("Finish the process of replacing the league with ID {0} for the tournament with ID {1}.", leagueId, tournamentId);
        return tournamentEntity.get();
    }

    @Transactional
    public void removeLeague(Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the league from the tournament with ID {0}", tournamentId);
        if (tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        LeagueEntity league = tournamentEntity.get().getLeague();
        if (league != null) {
            leagueRepository.findById(league.getId()).get().getTournaments().remove(tournamentEntity.get());
        }
        tournamentEntity.get().setLeague(null);
        log.info("Finish the process of disassociating the league from the tournament with ID {0}", tournamentId);
    }
}
