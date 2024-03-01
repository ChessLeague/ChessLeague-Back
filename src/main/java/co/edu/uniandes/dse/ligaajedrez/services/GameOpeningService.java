package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.OpeningEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.GameRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.OpeningRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GameOpeningService {
    @Autowired
    private OpeningRepository openingRepository;

    @Autowired
    private GameRepository gameRepository;

    @Transactional
    public OpeningEntity addOpening(Long gameId, Long openingId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the opening with ID {0} to the game with ID {1}.", openingId, gameId);
        if (gameId == null || gameId == 0L || openingId == null || openingId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + Long.toString(gameId) + " was not found.");
        }
        Optional<OpeningEntity> openingEntity = openingRepository.findById(openingId);
        if (openingEntity.isEmpty()) {
            throw new EntityNotFoundException("The opening with ID = " + Long.toString(openingId) + " was not found.");
        }
        gameEntity.get().setOpening(openingEntity.get());
        log.info("Finish the process of associating the opening with ID {0} to the game with ID {1}.", openingId, gameId);
        return openingEntity.get();
    }

    @Transactional
    public OpeningEntity getOpening(Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the opening of the game with ID {0}", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + Long.toString(gameId) + " was not found.");
        }
        OpeningEntity openingEntity = gameEntity.get().getOpening();
        if (openingEntity == null) {
            throw new EntityNotFoundException("The opening of the game with ID = " + Long.toString(gameId) + " was not found.");
        }
        log.info("Finish the process of querying the opening of the game with ID {0}", gameId);
        return openingEntity;
    }

    @Transactional
    public GameEntity replaceOpening(Long gameId, Long openingId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the opening with ID {0} for the game with ID {1}.", openingId, gameId);
        if (gameId == null || gameId == 0L || openingId == null || openingId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + Long.toString(gameId) + " was not found.");
        }
        Optional<OpeningEntity> openingEntity = openingRepository.findById(openingId);
        if (openingEntity.isEmpty()) {
            throw new EntityNotFoundException("The opening with ID = " + Long.toString(openingId) + " was not found.");
        }
        gameEntity.get().setOpening(openingEntity.get());
        log.info("Finish the process of replacing the opening with ID {0} for the game with ID {1}.", openingId, gameId);
        return gameEntity.get();
    }

    @Transactional
    public void removeOpening(Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the opening from the game with ID {0}", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + Long.toString(gameId) + " was not found.");
        }
        gameEntity.get().setOpening(null);
        log.info("Finish the process of disassociating the opening from the game with ID {0}", gameId);
    }
}
