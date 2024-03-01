package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.PlayerEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.PlayerRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlayerService {
    @Autowired
    PlayerRepository playerRepository;

    @Transactional
    public PlayerEntity createPlayer(PlayerEntity playerEntity) throws IllegalOperationException {
        log.info("Start the player creation process.");
        if (playerEntity.getId() == null || playerEntity.getId() == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Calendar calendar = Calendar.getInstance();
        if (playerEntity.getBirthDate() != null && playerEntity.getBirthDate().compareTo(calendar.getTime()) > 0) {
            throw new IllegalOperationException("The birthdate cannot be after the current date.");
        }
        try {
            log.info("Finish the player creation process.");
            return playerRepository.save(playerEntity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public List<PlayerEntity> getPlayers() {
        log.info("Start the process of querying all players.");
        return playerRepository.findAll();
    }

    @Transactional
    public PlayerEntity getPlayer(Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the player with ID = {0}.", playerId);
        if (playerId == null || playerId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        log.info("Finish the process of querying the player with ID = {0}.", playerId);
        return playerEntity.get();
    }

    @Transactional
    public PlayerEntity updatePlayer(Long playerId, PlayerEntity player) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of updating the player with ID = {0}.", playerId);
        if (playerId == null || playerId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        Calendar calendar = Calendar.getInstance();
        if (player.getBirthDate() != null && player.getBirthDate().compareTo(calendar.getTime()) > 0) {
            throw new IllegalOperationException("The birthdate cannot be after the current date.");
        }
        player.setId(playerId);
        try {
            log.info("Finish the process of updating the player with ID = {0}.", playerId);
            return playerRepository.saveAndFlush(player);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public void deletePlayer(Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of deleting the player with ID = {0}.", playerId);
        if (playerId == null || playerId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        playerRepository.deleteById(playerId);
        log.info("Finish the process of deleting the player with ID = {0}.", playerId);
    }
}
