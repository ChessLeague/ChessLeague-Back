package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.AdministratorEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.LeagueEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.AdministratorRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.LeagueRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LeagueAdministratorService {
    @Autowired
    private LeagueRepository leagueRepository;
    
    @Autowired
    private AdministratorRepository administratorRepository;

    @Transactional
    public AdministratorEntity addAdministrator(Long administratorId, Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the administrator with ID {0} to the league with ID {1}.", administratorId, leagueId);
        if (administratorId == null || administratorId == 0L || leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId)  + " was not found.");
        }
        leagueEntity.get().getAdministrators().add(administratorEntity.get());
        log.info("Finish the process of associating the administrator with ID {0} to the league with ID {1}.", administratorId, leagueId);
        return administratorEntity.get();
    }

    @Transactional
    public List<AdministratorEntity> getAdministrators(Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all administrators of the league with ID {0}.", leagueId);
        if (leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId)  + " was not found.");
        }
        log.info("Finish the process of querying all administrators of the league with ID {0}.", leagueId);
        return leagueEntity.get().getAdministrators();
    }

    @Transactional
    public AdministratorEntity getAdministrator(Long administratorId, Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the administrator with ID {0} of the league with ID {1}.", administratorId, leagueId);
        if (administratorId == null || administratorId == 0L || leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId)  + " was not found.");
        }
        if (!administratorEntity.get().getLeagues().contains(leagueEntity.get())) {
            throw new IllegalOperationException("The administrator is not associated with the league.");
        }
        log.info("Finish the process of querying the administrator with ID {0} of the league with ID {1}.", administratorId, leagueId);
        return administratorEntity.get();
    }

    @Transactional
    public List<AdministratorEntity> replaceAdministrators(Long leagueId, List<AdministratorEntity> administrators) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the administrators associated with the league with ID = {0}.", leagueId);
        if (leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId)  + " was not found.");
        }
        for (AdministratorEntity administrator : administrators) {
            Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administrator.getId());
            if (administratorEntity.isEmpty()) {
                throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administrator.getId())  + " was not found.");
            }
            if (!administratorEntity.get().getLeagues().contains(leagueEntity.get())) {
                administratorEntity.get().getLeagues().add(leagueEntity.get());
            }
        }
        leagueEntity.get().setAdministrators(administrators);
        log.info("Finish the process of replacing the administrators associated with the league with ID = {0}.", leagueId);
        return leagueEntity.get().getAdministrators();
    }

    @Transactional
    public void removeAdministrator(Long administratorId, Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the administrator with ID {0} from the league with ID {1}.", administratorId, leagueId);
        if (administratorId == null || administratorId == 0L || leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId)  + " was not found.");
        }
        if (!administratorEntity.get().getLeagues().contains(leagueEntity.get())) {
            throw new IllegalOperationException("The administrator is not associated with the league.");
        }
        leagueEntity.get().getAdministrators().remove(administratorEntity.get());
        administratorEntity.get().getLeagues().remove(leagueEntity.get());
        log.info("Finish the process of disassociating the administrator with ID {0} from the league with ID {1}.", administratorId, leagueId);
    }
}
