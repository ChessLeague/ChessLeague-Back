package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.TournamentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.TournamentRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TournamentService {
    @Autowired
    TournamentRepository tournamentRepository;

    @Transactional
    public TournamentEntity createTournament(TournamentEntity tournamentEntity) throws IllegalOperationException {
        log.info("Start the tournament creation process.");
        if (tournamentEntity.getId() == null || tournamentEntity.getId() == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Calendar calendar = Calendar.getInstance();
        if (tournamentEntity.getDate() != null && tournamentEntity.getDate().compareTo(calendar.getTime()) > 0) {
            throw new IllegalOperationException("The date cannot be after the current date.");
        }
        try {
            log.info("Finish the tournament creation process.");
            return tournamentRepository.save(tournamentEntity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public List<TournamentEntity> getTournaments() {
        log.info("Start the process of querying all tournaments.");
        return tournamentRepository.findAll();
    }

    @Transactional
    public TournamentEntity getTournament(Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the tournament with ID = {0}.", tournamentId);
        if (tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + tournamentId + " was not found.");
        }
        log.info("Finish the process of querying the tournament with ID = {0}.", tournamentId);
        return tournamentEntity.get();
    }

    @Transactional
    public TournamentEntity updateTournament(Long tournamentId, TournamentEntity tournament) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of updating the tournament with ID = {0}.", tournamentId);
        if (tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + tournamentId + " was not found.");
        }
        Calendar calendar = Calendar.getInstance();
        if (tournament.getDate() != null && tournament.getDate().compareTo(calendar.getTime()) > 0) {
            throw new IllegalOperationException("The date cannot be after the current date.");
        }
        tournament.setId(tournamentId);
        try {
            log.info("Finish the process of updating the tournament with ID = {0}.", tournamentId);
            return tournamentRepository.saveAndFlush(tournament);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public void deleteTournament(Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of deleting the tournament with ID = {0}.", tournamentId);
        if (tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + tournamentId + " was not found.");
        }
        tournamentRepository.deleteById(tournamentId);
        log.info("Finish the process of deleting the tournament with ID = {0}.", tournamentId);
    }
}
