package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.List;
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
public class TournamentGameService {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private GameRepository gameRepository;

    @Transactional
    public GameEntity addGame(Long tournamentId, Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the game with ID {0} to the tournament with ID {1}.", gameId, tournamentId);
        if (tournamentId == null || tournamentId == 0L || gameId == null || gameId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + Long.toString(gameId) + " was not found.");
        }
        tournamentEntity.get().getGames().add(gameEntity.get());
        log.info("Finish the process of associating the game with ID {0} to the tournament with ID {1}.", gameId, tournamentId);
        return gameEntity.get();
    }

    @Transactional
    public List<GameEntity> getGames(Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all games of the tournament with ID {0}.", tournamentId);
        if (tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        log.info("Finish the process of querying all games of the tournament with ID {0}.", tournamentId);
        return tournamentEntity.get().getGames();
    }

    @Transactional
    public GameEntity getGame(Long gameId, Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the game with ID {0} in the tournament with ID {1}.", gameId, tournamentId);
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
            throw new IllegalOperationException("The game is not associated with the tournament.");
        }
        log.info("Finish the process of querying the game with ID {0} in the tournament with ID {1}.", gameId, tournamentId);
        return gameEntity.get();
    }

    @Transactional
    public List<GameEntity> replaceGames(Long tournamentId, List<GameEntity> games) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the games associated with the tournament with ID = {0}.", tournamentId);
        if (tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        for (GameEntity game : games) {
            Optional<GameEntity> gameEntity = gameRepository.findById(game.getId());
            if (gameEntity.isEmpty()) {
                throw new EntityNotFoundException("The game with ID = " + Long.toString(game.getId()) + " was not found.");
            }
            gameEntity.get().setTournament(tournamentEntity.get());
        }
        tournamentEntity.get().setGames(games);
        log.info("Finish the process of replacing the games associated with the tournament with ID = {0}.", tournamentId);
        return tournamentEntity.get().getGames();
    }

    @Transactional
    public void removeGame(Long tournamentId, Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the game with ID {0} from the tournament with ID {1}.", gameId, tournamentId);
        if (tournamentId == null || tournamentId == 0L || gameId == null || gameId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + Long.toString(tournamentId) + " was not found.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + Long.toString(gameId) + " was not found.");
        }
        if (!tournamentEntity.get().getGames().contains(gameEntity.get())) {
            throw new IllegalOperationException("The game is not associated with the tournament.");
        }
        tournamentEntity.get().getGames().remove(gameEntity.get());
        gameEntity.get().setTournament(null);
        log.info("Finish the process of disassociating the game with ID {0} from the tournament with ID {1}.", gameId, tournamentId);
    }
}
