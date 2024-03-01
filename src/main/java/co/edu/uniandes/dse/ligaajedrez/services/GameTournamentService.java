package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.TournamentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.GameRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.TournamentRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GameTournamentService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Transactional
    public TournamentEntity addTournament(Long gameId, Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the tournament with ID {0} to the game with ID {1}.", tournamentId, gameId);
        if (gameId == null || gameId == 0L || tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + Long.toString(gameId) + " was not found.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        gameEntity.get().setTournament(tournamentEntity.get());
        log.info("Finish the process of associating the tournament with ID {0} to the game with ID {1}.", tournamentId, gameId);
        return tournamentEntity.get();
    }

    @Transactional
    public TournamentEntity getTournament(Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the tournament of the game with ID {0}", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + Long.toString(gameId) + " was not found.");
        }
        TournamentEntity tournamentEntity = gameEntity.get().getTournament();
        if (tournamentEntity == null) {
            throw new EntityNotFoundException("The tournament of the game with ID = " + Long.toString(gameId) + " was not found.");
        }
        log.info("Finish the process of querying the tournament of the game with ID {0}", gameId);
        return tournamentEntity;
    }

    @Transactional
    public TournamentEntity replaceTournament(Long gameId, Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the tournament with ID {0} for the game with ID {1}.", tournamentId, gameId);
        if (gameId == null || gameId == 0L || tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + Long.toString(gameId) + " was not found.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        if (!tournamentEntity.get().getGames().contains(gameEntity.get())) {
            tournamentEntity.get().getGames().add(gameEntity.get());
        }
        gameEntity.get().setTournament(tournamentEntity.get());
        log.info("Finish the process of replacing the tournament with ID {0} for the game with ID {1}.", tournamentId, gameId);
        return tournamentEntity.get();
    }

    @Transactional
    public void removeTournament(Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the tournament from the game with ID {0}", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + Long.toString(gameId) + " was not found.");
        }
        TournamentEntity tournament = gameEntity.get().getTournament();
        if (tournament != null) {
            tournamentRepository.findById(tournament.getId()).get().getGames().remove(gameEntity.get());
        }
        gameEntity.get().setTournament(null);
        log.info("Finish the process of disassociating the tournament from the game with ID {0}", gameId);
    }
}
