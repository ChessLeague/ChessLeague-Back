package co.edu.uniandes.dse.ligaajedrez.services;

import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.PlayerEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.GameRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PlayerGameService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Transactional
    public GameEntity addGame(Long playerId, Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the game with ID {0} to the player with ID {1}.", gameId, playerId);
        if (playerId == null || playerId == 0L || gameId == null || gameId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        playerEntity.get().getGames().add(gameEntity.get());
        log.info("Finish the process of associating the game with ID {0} to the player with ID {1}.", gameId, playerId);
        return gameEntity.get();
    }

    @Transactional
    public List<GameEntity> getGames(Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all games of the player with ID {0}.", playerId);
        if (playerId == null || playerId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        log.info("Finish the process of querying all games of the player with ID {0}.", playerId);
        return playerEntity.get().getGames();
    }

    @Transactional
    public GameEntity getGame(Long gameId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the game with ID {0} for the player with ID {1}.", gameId, playerId);
        if (gameId == null || gameId == 0L || playerId == null || playerId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        if (!playerEntity.get().getGames().contains(gameEntity.get())) {
            throw new IllegalOperationException("The game is not associated with the player.");
        }
        log.info("Finish the process of querying the game with ID {0} for the player with ID {1}.", gameId, playerId);
        return gameEntity.get();
    }

    @Transactional
    public List<GameEntity> replaceGames(Long playerId, List<GameEntity> games) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the games associated with the player with ID = {0}.", playerId);
        if (playerId == null || playerId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        for (GameEntity game : games) {
            Optional<GameEntity> gameEntity = gameRepository.findById(game.getId());
            if (gameEntity.isEmpty()) {
                throw new EntityNotFoundException("The game with ID = " + game.getId() + " was not found.");
            }
            if (!gameEntity.get().getPlayers().contains(playerEntity.get())) {
                gameEntity.get().getPlayers().add(playerEntity.get());
            }
        }
        playerEntity.get().setGames(games);
        log.info("Finish the process of replacing the games associated with the player with ID = {0}.", playerId);
        return playerEntity.get().getGames();
    }

    @Transactional
    public void removeGame(Long playerId, Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the game with ID {0} from the player with ID {1}.", gameId, playerId);
        if (playerId == null || playerId == 0L || gameId == null || gameId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        if (!playerEntity.get().getGames().contains(gameEntity.get())) {
            throw new IllegalOperationException("The game is not associated with the player.");
        }
        playerEntity.get().getGames().remove(gameEntity.get());
        gameEntity.get().getPlayers().remove(playerEntity.get());
        log.info("Finish the process of disassociating the game with ID {0} from the player with ID {1}.", gameId, playerId);
    }
}
