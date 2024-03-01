package co.edu.uniandes.dse.ligaajedrez.services;

import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.MoveEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.GameRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.MoveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GameMoveService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MoveRepository moveRepository;

    @Transactional
    public MoveEntity addMove(Long gameId, Long moveId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the move with ID {0} to the game with ID {1}.", moveId, gameId);
        if (gameId == null || gameId == 0L || moveId == null || moveId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + moveId + " was not found.");
        }
        gameEntity.get().getMoves().add(moveEntity.get());
        log.info("Finish the process of associating the move with ID {0} to the game with ID {1}.", moveId, gameId);
        return moveEntity.get();
    }

    @Transactional
    public List<MoveEntity> getMoves(Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all moves of the game with ID {0}.", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        log.info("Finish the process of querying all moves of the game with ID {0}.", gameId);
        return gameEntity.get().getMoves();
    }

    @Transactional
    public MoveEntity getMove(Long moveId, Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the move with ID {0} in the game with ID {1}.", moveId, gameId);
        if (moveId == null || moveId == 0L || gameId == null || gameId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + moveId + " was not found.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        if (!gameEntity.get().getMoves().contains(moveEntity.get())) {
            throw new IllegalOperationException("The move is not associated with the game.");
        }
        log.info("Finish the process of querying the move with ID {0} in the game with ID {1}.", moveId, gameId);
        return moveEntity.get();
    }

    @Transactional
    public List<MoveEntity> replaceMoves(Long gameId, List<MoveEntity> moves) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the moves associated with the game with ID = {0}.", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        for (MoveEntity move : moves) {
            Optional<MoveEntity> moveEntity = moveRepository.findById(move.getId());
            if (moveEntity.isEmpty()) {
                throw new EntityNotFoundException("The move with ID = " + move.getId() + " was not found.");
            }
        }
        gameEntity.get().setMoves(moves);
        log.info("Finish the process of replacing the moves associated with the game with ID = {0}.", gameId);
        return gameEntity.get().getMoves();
    }

    @Transactional
    public void removeMove(Long gameId, Long moveId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the move with ID {0} from the game with ID {1}.", moveId, gameId);
        if (gameId == null || gameId == 0L || moveId == null || moveId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + moveId + " was not found.");
        }
        if (!gameEntity.get().getMoves().contains(moveEntity.get())) {
            throw new IllegalOperationException("The move is not associated with the game.");
        }
        gameEntity.get().getMoves().remove(moveEntity.get());
        log.info("Finish the process of disassociating the move with ID {0} from the game with ID {1}.", moveId, gameId);
    }
}
