package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.MoveEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.MoveRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MoveService {
    @Autowired
    MoveRepository moveRepository;

    @Transactional
    public MoveEntity createMove(MoveEntity moveEntity) throws IllegalOperationException {
        log.info("Start the move creation process.");
        if (moveEntity.getId() == null || moveEntity.getId() == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        if (moveEntity.getNotation() == null || !isValidChessNotation(moveEntity.getNotation())) {
            throw new IllegalOperationException("Invalid Notation");
        }
        try {
            log.info("Finish the move creation process.");
            return moveRepository.save(moveEntity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public List<MoveEntity> getMoves() {
        log.info("Start the process of querying all moves.");
        return moveRepository.findAll();
    }

    @Transactional
    public MoveEntity getMove(Long moveId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the move with ID = {0}.", moveId);
        if (moveId == null || moveId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + moveId + " was not found.");
        }
        log.info("Finish the process of querying the move with ID = {0}.", moveId);
        return moveEntity.get();
    }

    @Transactional
    public MoveEntity updateMove(Long moveId, MoveEntity move) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of updating the move with ID = {0}.", moveId);
        if (moveId == null || moveId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + moveId + " was not found.");
        }
        if (move.getNotation() == null || !isValidChessNotation(move.getNotation())) {
            throw new IllegalOperationException("Invalid Notation");
        }
        move.setId(moveId);
        try {
            log.info("Finish the process of updating the move with ID = {0}.", moveId);
            return moveRepository.saveAndFlush(move);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public void deleteMove(Long moveId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of deleting the move with ID = {0}.", moveId);
        if (moveId == null || moveId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<MoveEntity> moveEntity = moveRepository.findById(moveId);
        if (moveEntity.isEmpty()) {
            throw new EntityNotFoundException("The move with ID = " + moveId + " was not found.");
        }
        moveRepository.deleteById(moveId);
        log.info("Finish the process of deleting the move with ID = {0}.", moveId);
    }

    public boolean isValidChessNotation(String notation) {
        final String REGEX = "^([KQRBNP]?[a-h]?[1-8]?x?[a-h][1-8](=[KQRBNP])?[+#!]?|O-O(-O)?[+#!]?)$";
        return Pattern.matches(REGEX, notation);
    }
}
