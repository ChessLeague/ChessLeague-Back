package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.LeagueEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.LeagueRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LeagueService {
    @Autowired
    LeagueRepository leagueRepository;

    @Transactional
    public LeagueEntity createLeague(LeagueEntity leagueEntity) throws IllegalOperationException {
        log.info("Start the league creation process.");
        if (leagueEntity.getId() == null || leagueEntity.getId() == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        try {
            log.info("Finish the league creation process.");
            return leagueRepository.save(leagueEntity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public List<LeagueEntity> getLeagues() {
        log.info("Start the process of querying all leagues.");
        return leagueRepository.findAll();
    }

    @Transactional
    public LeagueEntity getLeague(Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the league with ID = {0}.", leagueId);
        if (leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + leagueId + " was not found.");
        }
        log.info("Finish the process of querying the league with ID = {0}.", leagueId);
        return leagueEntity.get();
    }

    @Transactional
    public LeagueEntity updateLeague(Long leagueId, LeagueEntity league) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of updating the league with ID = {0}.", leagueId);
        if (leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + leagueId + " was not found.");
        }
        league.setId(leagueId);
        try {
            log.info("Finish the process of updating the league with ID = {0}.", leagueId);
            return leagueRepository.saveAndFlush(league);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public void deleteLeague(Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of deleting the league with ID = {0}.", leagueId);
        if (leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + leagueId + " was not found.");
        }
        leagueRepository.deleteById(leagueId);
        log.info("Finish the process of deleting the league with ID = {0}.", leagueId);
    }
}
