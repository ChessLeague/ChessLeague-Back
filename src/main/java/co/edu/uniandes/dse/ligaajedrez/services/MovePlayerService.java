package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.MoveEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.PlayerEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.MoveRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.PlayerRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MovePlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MoveRepository moveRepository;

    @Transactional
    public MoveEntity addPlayer(Long moveId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the move with ID {0} to the player with ID {1}.", moveId, playerId);
        if (moveId == null || moveId == 0L || playerId == null || playerId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + Long.toString(moveId) + " was not found.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + Long.toString(playerId) + " was not found.");
        }
        moveEntity.get().setPlayer(playerEntity.get());
        log.info("Finish the process of associating the move with ID {0} to the player with ID {1}.", moveId, playerId);
        return moveEntity.get();
    }

    @Transactional
    public PlayerEntity getPlayer(Long moveId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the player of the move with ID {0}", moveId);
        if (moveId == null || moveId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + Long.toString(moveId) + " was not found.");
        }
        PlayerEntity playerEntity = moveEntity.get().getPlayer();
        if (playerEntity == null) {
            throw new EntityNotFoundException("The player of the move with ID = " + Long.toString(moveId) + " was not found.");
        }
        log.info("Finish the process of querying the player of the move with ID {0}", moveId);
        return playerEntity;
    }

    @Transactional
    public MoveEntity replacePlayer(Long moveId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the player with ID {0} for the move with ID {1}.", playerId, moveId);
        if (moveId == null || moveId == 0L || playerId == null || playerId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + Long.toString(moveId) + " was not found.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + Long.toString(playerId) + " was not found.");
        }
        moveEntity.get().setPlayer(playerEntity.get());
        log.info("Finish the process of replacing the player with ID {0} for the move with ID {1}.", playerId, moveId);
        return moveEntity.get();
    }

    @Transactional
    public void removePlayer(Long moveId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the player from the move with ID {0}", moveId);
        if (moveId == null || moveId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + Long.toString(moveId) + " was not found.");
        }
        moveEntity.get().setPlayer(null);
        log.info("Finish the process of disassociating the player from the move with ID {0}", moveId);
    }
}
