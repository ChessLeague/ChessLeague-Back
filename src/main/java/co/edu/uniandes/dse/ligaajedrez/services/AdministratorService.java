package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.AdministratorEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.AdministratorRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdministratorService {
    @Autowired
    AdministratorRepository administratorRepository;

    @Transactional
    public AdministratorEntity createAdministrator(AdministratorEntity administratorEntity) throws IllegalOperationException {
        log.info("Start the administrator creation process.");
        try {
            log.info("Finish the administrator creation process.");
            return administratorRepository.save(administratorEntity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public List<AdministratorEntity> getAdministrators() {
        log.info("Start the process of querying all administrators.");
        return administratorRepository.findAll();
    }

    @Transactional
    public AdministratorEntity getAdministrator(Long administratorId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the administrator with ID = {0}.", administratorId);
        if (administratorId == null || administratorId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        log.info("Finish the process of querying the administrator with ID = {0}.", administratorId);
        return administratorEntity.get();
    }

    @Transactional
    public AdministratorEntity updateAdministrator(Long administratorId, AdministratorEntity administrator) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of updating the administrator with ID = {0}.", administratorId);
        if (administratorId == null || administratorId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        administrator.setId(administratorId);
        try {
            log.info("Finish the process of updating the administrator with ID = {0}.", administratorId);
            return administratorRepository.saveAndFlush(administrator);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public void deleteAdministrator(Long administratorId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of deleting the administrator with ID = {0}.", administratorId);
        if (administratorId == null || administratorId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        administratorRepository.deleteById(administratorId);
        log.info("Finish the process of deleting the administrator with ID = {0}.", administratorId);
    }
}
