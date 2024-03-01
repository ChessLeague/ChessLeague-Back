package co.edu.uniandes.dse.ligaajedrez.services;

import co.edu.uniandes.dse.ligaajedrez.entities.MoveEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.OpeningEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.MoveRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.OpeningRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OpeningMoveService {
    @Autowired
    private OpeningRepository openingRepository;

    @Autowired
    private MoveRepository moveRepository;

    @Transactional
    public MoveEntity addMove(Long openingId, Long moveId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the move with ID {0} to the opening with ID {1}.", moveId, openingId);
        if (openingId == null || openingId == 0L || moveId == null || moveId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<OpeningEntity> openingEntity = openingRepository.findById(openingId);
        if (openingEntity.isEmpty()) {
            throw new EntityNotFoundException("The opening with ID = " + openingId + " was not found.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + moveId + " was not found.");
        }
        openingEntity.get().getMoves().add(moveEntity.get());
        log.info("Finish the process of associating the move with ID {0} to the opening with ID {1}.", moveId, openingId);
        return moveEntity.get();
    }

    @Transactional
    public List<MoveEntity> getMoves(Long openingId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all moves of the opening with ID {0}.", openingId);
        if (openingId == null || openingId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<OpeningEntity> openingEntity = openingRepository.findById(openingId);
        if (openingEntity.isEmpty()) {
            throw new EntityNotFoundException("The opening with ID = " + openingId + " was not found.");
        }
        log.info("Finish the process of querying all moves of the opening with ID {0}.", openingId);
        return openingEntity.get().getMoves();
    }

    @Transactional
    public MoveEntity getMove(Long moveId, Long openingId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the move with ID {0} in the opening with ID {1}.", moveId, openingId);
        if (moveId == null || moveId == 0L || openingId == null || openingId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + moveId + " was not found.");
        }
        Optional<OpeningEntity> openingEntity = openingRepository.findById(openingId);
        if (openingEntity.isEmpty()) {
            throw new EntityNotFoundException("The opening with ID = " + openingId + " was not found.");
        }
        if (!openingEntity.get().getMoves().contains(moveEntity.get())) {
            throw new IllegalOperationException("The move is not associated with the opening.");
        }
        log.info("Finish the process of querying the move with ID {0} in the opening with ID {1}.", moveId, openingId);
        return moveEntity.get();
    }

    @Transactional
    public List<MoveEntity> replaceMoves(Long openingId, List<MoveEntity> moves) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the moves associated with the opening with ID = {0}.", openingId);
        if (openingId == null || openingId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<OpeningEntity> openingEntity = openingRepository.findById(openingId);
        if (openingEntity.isEmpty()) {
            throw new EntityNotFoundException("The opening with ID = " + openingId + " was not found.");
        }
        for (MoveEntity move : moves) {
            Optional<MoveEntity> moveEntity = moveRepository.findById(move.getId());
            if (moveEntity.isEmpty()) {
                throw new EntityNotFoundException("The move with ID = " + move.getId() + " was not found.");
            }
        }
        openingEntity.get().setMoves(moves);
        log.info("Finish the process of replacing the moves associated with the opening with ID = {0}.", openingId);
        return openingEntity.get().getMoves();
    }

    @Transactional
    public void removeMove(Long openingId, Long moveId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the move with ID {0} from the opening with ID {1}.", moveId, openingId);
        if (openingId == null || openingId == 0L || moveId == null || moveId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<OpeningEntity> openingEntity = openingRepository.findById(openingId);
        if (openingEntity.isEmpty()) {
            throw new EntityNotFoundException("The opening with ID = " + openingId + " was not found.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + moveId + " was not found.");
        }
        if (!openingEntity.get().getMoves().contains(moveEntity.get())) {
            throw new IllegalOperationException("The move is not associated with the opening.");
        }
        openingEntity.get().getMoves().remove(moveEntity.get());
        log.info("Finish the process of disassociating the move with ID {0} from the opening with ID {1}.", moveId, openingId);
    }
}
