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
public class AdministratorLeagueService {
    @Autowired
    private LeagueRepository leagueRepository;
    
    @Autowired
    private AdministratorRepository administratorRepository;

    @Transactional
    public LeagueEntity addLeague(Long leagueId, Long administratorId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the league with ID {0} to the administrator with ID {1}.", leagueId, administratorId);
        if (leagueId == null || leagueId == 0L || administratorId == null || administratorId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        administratorEntity.get().getLeagues().add(leagueEntity.get());
        log.info("Finish the process of associating the league with ID {0} to the administrator with ID {1}.", leagueId, administratorId);
        return leagueEntity.get();
    }

    @Transactional
    public List<LeagueEntity> getLeagues(Long administratorId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all leagues of the administrator with ID {0}.", administratorId);
        if (administratorId == null || administratorId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        log.info("Finish the process of querying all leagues of the administrator with ID {0}.", administratorId);
        return administratorEntity.get().getLeagues();
    }

    @Transactional
    public LeagueEntity getLeague(Long leagueId, Long administratorId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of queryng the league with ID {0} of the administrator with ID {1}.", leagueId, administratorId);
        if (leagueId == null || leagueId == 0L || administratorId == null || administratorId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        if (!leagueEntity.get().getAdministrators().contains(administratorEntity.get())) {
            throw new IllegalOperationException("The league is not associted to the administrator.");
        }
        log.info("Finish the process of queryng the league with ID {0} of the administrator with ID {1}.", leagueId, administratorId);
        return leagueEntity.get();
    }

    @Transactional
    public List<LeagueEntity> replaceLeagues(Long administratorId, List<LeagueEntity> leagues) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the leagues associated with the administrator with ID = {0}.", administratorId);
        if (administratorId == null || administratorId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        for (LeagueEntity league : leagues) {
            Optional<LeagueEntity> leagueEntity = leagueRepository.findById(league.getId());
            if (leagueEntity.isEmpty()) {
                throw new EntityNotFoundException("The league with ID = " + Long.toString(administratorId)  + " was not found.");
            }
            if (!leagueEntity.get().getAdministrators().contains(administratorEntity.get())) {
                leagueEntity.get().getAdministrators().add(administratorEntity.get());
            }
        }
        administratorEntity.get().setLeagues(leagues);
        log.info("Finish the process of replacing the leagues associated with the administrator with ID = {0}.", administratorId);
        return administratorEntity.get().getLeagues();
    }

    @Transactional
    public void removeLeague(Long leagueId, Long administratorId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of deleting the league with ID {0} of the administrator with ID {1}.", leagueId, administratorId);
        if (leagueId == null || leagueId == 0L || administratorId == null || administratorId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        if (!leagueEntity.get().getAdministrators().contains(administratorEntity.get())) {
            throw new IllegalOperationException("The league is not associted to the administrator.");
        }
        administratorEntity.get().getLeagues().remove(leagueEntity.get());
        leagueEntity.get().getAdministrators().remove(administratorEntity.get());
        log.info("Finish the process of deleting the league with ID {0} of the administrator with ID {1}.", leagueId, administratorId);
    }
}
