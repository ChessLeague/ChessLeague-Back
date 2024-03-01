package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.List;
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
public class LeagueTournamentService {
    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Transactional
    public TournamentEntity addTournament(Long leagueId, Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the tournament with ID {0} to the league with ID {1}.", tournamentId, leagueId);
        if (leagueId == null || leagueId == 0L || tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        leagueEntity.get().getTournaments().add(tournamentEntity.get());
        log.info("Finish the process of associating the tournament with ID {0} to the league with ID {1}.", tournamentId, leagueId);
        return tournamentEntity.get();
    }

    @Transactional
    public List<TournamentEntity> getTournaments(Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all tournaments of the league with ID {0}.", leagueId);
        if (leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        log.info("Finish the process of querying all tournaments of the league with ID {0}.", leagueId);
        return leagueEntity.get().getTournaments();
    }

    @Transactional
    public TournamentEntity getTournament(Long tournamentId, Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the tournament with ID {0} in the league with ID {1}.", tournamentId, leagueId);
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
            throw new IllegalOperationException("The tournament is not associated with the league.");
        }
        log.info("Finish the process of querying the tournament with ID {0} in the league with ID {1}.", tournamentId, leagueId);
        return tournamentEntity.get();
    }

    @Transactional
    public List<TournamentEntity> replaceTournaments(Long leagueId, List<TournamentEntity> tournaments) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the tournaments associated with the league with ID = {0}.", leagueId);
        if (leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        for (TournamentEntity tournament : tournaments) {
            Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournament.getId());
            if (tournamentEntity.isEmpty()) {
                throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournament.getId()) + " was not found.");
            }
            tournamentEntity.get().setLeague(leagueEntity.get());
        }
        leagueEntity.get().setTournaments(tournaments);
        log.info("Finish the process of replacing the tournaments associated with the league with ID = {0}.", leagueId);
        return leagueEntity.get().getTournaments();
    }

    @Transactional
    public void removeTournament(Long leagueId, Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the tournament with ID {0} from the league with ID {1}.", tournamentId, leagueId);
        if (leagueId == null || leagueId == 0L || tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        if (!leagueEntity.get().getTournaments().contains(tournamentEntity.get())) {
            throw new IllegalOperationException("The tournament is not associated with the league.");
        }
        leagueEntity.get().getTournaments().remove(tournamentEntity.get());
        tournamentEntity.get().setLeague(null);
        log.info("Finish the process of disassociating the tournament with ID {0} from the league with ID {1}.", tournamentId, leagueId);
    }
}
