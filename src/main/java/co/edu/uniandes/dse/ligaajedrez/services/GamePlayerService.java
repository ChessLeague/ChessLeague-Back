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
public class GamePlayerService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public PlayerEntity addPlayer(Long gameId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the player with ID {0} to the game with ID {1}.", playerId, gameId);
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
        gameEntity.get().getPlayers().add(playerEntity.get());
        log.info("Finish the process of associating the player with ID {0} to the game with ID {1}.", playerId, gameId);
        return playerEntity.get();
    }

    @Transactional
    public List<PlayerEntity> getPlayers(Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all players of the game with ID {0}.", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        log.info("Finish the process of querying all players of the game with ID {0}.", gameId);
        return gameEntity.get().getPlayers();
    }

    @Transactional
    public PlayerEntity getPlayer(Long playerId, Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the player with ID {0} in the game with ID {1}.", playerId, gameId);
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
        if (!gameEntity.get().getPlayers().contains(playerEntity.get())) {
            throw new IllegalOperationException("The player is not associated with the game.");
        }
        log.info("Finish the process of querying the player with ID {0} in the game with ID {1}.", playerId, gameId);
        return playerEntity.get();
    }

    @Transactional
    public List<PlayerEntity> replacePlayers(Long gameId, List<PlayerEntity> players) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the players associated with the game with ID = {0}.", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        for (PlayerEntity player : players) {
            Optional<PlayerEntity> playerEntity = playerRepository.findById(player.getId());
            if (playerEntity.isEmpty()) {
                throw new EntityNotFoundException("The player with ID = " + player.getId() + " was not found.");
            }
            if (!playerEntity.get().getGames().contains(gameEntity.get())) {
                playerEntity.get().getGames().add(gameEntity.get());
            }
        }
        gameEntity.get().setPlayers(players);
        log.info("Finish the process of replacing the players associated with the game with ID = {0}.", gameId);
        return gameEntity.get().getPlayers();
    }

    @Transactional
    public void removePlayer(Long gameId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the player with ID {0} from the game with ID {1}.", playerId, gameId);
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
        if (!gameEntity.get().getPlayers().contains(playerEntity.get())) {
            throw new IllegalOperationException("The player is not associated with the game.");
        }
        gameEntity.get().getPlayers().remove(playerEntity.get());
        playerEntity.get().getGames().remove(gameEntity.get());
        log.info("Finish the process of disassociating the player with ID {0} from the game with ID {1}.", playerId, gameId);
    }
}
