package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.GameRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GameService {
    @Autowired
    GameRepository gameRepository;

    @Transactional
    public GameEntity createGame(GameEntity gameEntity) throws IllegalOperationException {
        log.info("Start the game creation process.");
        if (gameEntity.getId() == null || gameEntity.getId() == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Calendar calendar = Calendar.getInstance();
        if (gameEntity.getDate() == null || gameEntity.getDate().compareTo(calendar.getTime()) > 0) {
            throw new IllegalOperationException("The date cannot be null or after the current date.");
        }
        try {
            log.info("Finish the game creation process.");
            return gameRepository.save(gameEntity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public List<GameEntity> getGames(Pageable pageable) {
        log.info("Start the process of querying all games.");
        return gameRepository.findAll(pageable).getContent();
    }

    @Transactional
    public GameEntity getGame(Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the game with ID = {0}.", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        log.info("Finish the process of querying the game with ID = {0}.", gameId);
        return gameEntity.get();
    }

    @Transactional
    public GameEntity updateGame(Long gameId, GameEntity game) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of updating the game with ID = {0}.", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        Calendar calendar = Calendar.getInstance();
        if (game.getDate() == null || game.getDate().compareTo(calendar.getTime()) > 0) {
            throw new IllegalOperationException("The date cannot be null or after the current date.");
        }
        game.setId(gameId);
        try {
            log.info("Finish the process of updating the game with ID = {0}.", gameId);
            return gameRepository.saveAndFlush(game);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public void deleteGame(Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of deleting the game with ID = {0}.", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        gameRepository.deleteById(gameId);
        log.info("Finish the process of deleting the game with ID = {0}.", gameId);
    }

    @Transactional
    public long getTotalGameCount() {
        log.info("Start the process of counting all games.");
        return gameRepository.count();
    }

}
