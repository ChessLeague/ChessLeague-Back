package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.OpeningEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.OpeningRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OpeningService {
    @Autowired
    OpeningRepository openingRepository;

    @Transactional
    public OpeningEntity createOpening(OpeningEntity openingEntity) throws IllegalOperationException {
        log.info("Start the opening creation process.");
        if (openingEntity.getId() == null || openingEntity.getId() == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        try {
            log.info("Finish the opening creation process.");
            return openingRepository.save(openingEntity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public List<OpeningEntity> getOpenings() {
        log.info("Start the process of querying all openings.");
        return openingRepository.findAll();
    }

    @Transactional
    public OpeningEntity getOpening(Long openingId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the opening with ID = {0}.", openingId);
        if (openingId == null || openingId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<OpeningEntity> openingEntity = openingRepository.findById(openingId);
        if (openingEntity.isEmpty()) {
            throw new EntityNotFoundException("The opening with ID = " + openingId + " was not found.");
        }
        log.info("Finish the process of querying the opening with ID = {0}.", openingId);
        return openingEntity.get();
    }

    @Transactional
    public OpeningEntity updateOpening(Long openingId, OpeningEntity opening) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of updating the opening with ID = {0}.", openingId);
        if (openingId == null || openingId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<OpeningEntity> openingEntity = openingRepository.findById(openingId);
        if (openingEntity.isEmpty()) {
            throw new EntityNotFoundException("The opening with ID = " + openingId + " was not found.");
        }
        opening.setId(openingId);
        try {
            log.info("Finish the process of updating the opening with ID = {0}.", openingId);
            return openingRepository.saveAndFlush(opening);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public void deleteOpening(Long openingId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of deleting the opening with ID = {0}.", openingId);
        if (openingId == null || openingId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<OpeningEntity> openingEntity = openingRepository.findById(openingId);
        if (openingEntity.isEmpty()) {
            throw new EntityNotFoundException("The opening with ID = " + openingId + " was not found.");
        }
        openingRepository.deleteById(openingId);
        log.info("Finish the process of deleting the opening with ID = {0}.", openingId);
    }
}
