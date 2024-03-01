package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.LeagueEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.PlayerEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.LeagueRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.PlayerRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlayerLeagueService {
    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public LeagueEntity addLeague(Long leagueId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the league with ID {0} to the player with ID {1}.", leagueId, playerId);
        if (leagueId == null || leagueId == 0L || playerId == null || playerId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + Long.toString(playerId) + " was not found.");
        }
        playerEntity.get().getLeagues().add(leagueEntity.get());
        log.info("Finish the process of associating the league with ID {0} to the player with ID {1}.", leagueId, playerId);
        return leagueEntity.get();
    }

    @Transactional
    public List<LeagueEntity> getLeagues(Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all leagues of the player with ID {0}.", playerId);
        if (playerId == null || playerId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + Long.toString(playerId) + " was not found.");
        }
        log.info("Finish the process of querying all leagues of the player with ID {0}.", playerId);
        return playerEntity.get().getLeagues();
    }

    @Transactional
    public LeagueEntity getLeague(Long leagueId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the league with ID {0} of the player with ID {1}.", leagueId, playerId);
        if (leagueId == null || leagueId == 0L || playerId == null || playerId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + Long.toString(playerId) + " was not found.");
        }
        if (!leagueEntity.get().getPlayers().contains(playerEntity.get())) {
            throw new IllegalOperationException("The league is not associated with the player.");
        }
        log.info("Finish the process of querying the league with ID {0} of the player with ID {1}.", leagueId, playerId);
        return leagueEntity.get();
    }

    @Transactional
    public List<LeagueEntity> replaceLeagues(Long playerId, List<LeagueEntity> leagues) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the leagues associated with the player with ID = {0}.", playerId);
        if (playerId == null || playerId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + Long.toString(playerId) + " was not found.");
        }
        for (LeagueEntity league : leagues) {
            Optional<LeagueEntity> leagueEntity = leagueRepository.findById(league.getId());
            if (leagueEntity.isEmpty()) {
                throw new EntityNotFoundException("The league with ID = " + Long.toString(league.getId()) + " was not found.");
            }
            if (!leagueEntity.get().getPlayers().contains(playerEntity.get())) {
                leagueEntity.get().getPlayers().add(playerEntity.get());
            }
        }
        playerEntity.get().setLeagues(leagues);
        log.info("Finish the process of replacing the leagues associated with the player with ID = {0}.", playerId);
        return playerEntity.get().getLeagues();
    }

    @Transactional
    public void removeLeague(Long leagueId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of deleting the league with ID {0} of the player with ID {1}.", leagueId, playerId);
        if (leagueId == null || leagueId == 0L || playerId == null || playerId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + Long.toString(playerId) + " was not found.");
        }
        if (!leagueEntity.get().getPlayers().contains(playerEntity.get())) {
            throw new IllegalOperationException("The league is not associated with the player.");
        }
        playerEntity.get().getLeagues().remove(leagueEntity.get());
        leagueEntity.get().getPlayers().remove(playerEntity.get());
        log.info("Finish the process of deleting the league with ID {0} of the player with ID {1}.", leagueId, playerId);
    }
}
