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
public class LeaguePlayerService {
    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public PlayerEntity addPlayer(Long leagueId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the player with ID {0} to the league with ID {1}.", playerId, leagueId);
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
        leagueEntity.get().getPlayers().add(playerEntity.get());
        log.info("Finish the process of associating the player with ID {0} to the league with ID {1}.", playerId, leagueId);
        return playerEntity.get();
    }

    @Transactional
    public List<PlayerEntity> getPlayers(Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all players of the league with ID {0}.", leagueId);
        if (leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        log.info("Finish the process of querying all players of the league with ID {0}.", leagueId);
        return leagueEntity.get().getPlayers();
    }

    @Transactional
    public PlayerEntity getPlayer(Long playerId, Long leagueId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the player with ID {0} in the league with ID {1}.", playerId, leagueId);
        if (playerId == null || playerId == 0L || leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + Long.toString(playerId) + " was not found.");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        if (!leagueEntity.get().getPlayers().contains(playerEntity.get())) {
            throw new IllegalOperationException("The player is not associated with the league.");
        }
        log.info("Finish the process of querying the player with ID {0} in the league with ID {1}.", playerId, leagueId);
        return playerEntity.get();
    }

    @Transactional
    public List<PlayerEntity> replacePlayers(Long leagueId, List<PlayerEntity> players) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the players associated with the league with ID = {0}.", leagueId);
        if (leagueId == null || leagueId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<LeagueEntity> leagueEntity = leagueRepository.findById(leagueId);
        if (leagueEntity.isEmpty()) {
            throw new EntityNotFoundException("The league with ID = " + Long.toString(leagueId) + " was not found.");
        }
        for (PlayerEntity player : players) {
            Optional<PlayerEntity> playerEntity = playerRepository.findById(player.getId());
            if (playerEntity.isEmpty()) {
                throw new EntityNotFoundException("The player with ID = " + Long.toString(player.getId()) + " was not found.");
            }
            if (!leagueEntity.get().getPlayers().contains(playerEntity.get())) {
                leagueEntity.get().getPlayers().add(playerEntity.get());
            }
        }
        leagueEntity.get().setPlayers(players);
        log.info("Finish the process of replacing the players associated with the league with ID = {0}.", leagueId);
        return leagueEntity.get().getPlayers();
    }

    @Transactional
    public void removePlayer(Long leagueId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the player with ID {0} from the league with ID {1}.", playerId, leagueId);
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
            throw new IllegalOperationException("The player is not associated with the league.");
        }
        leagueEntity.get().getPlayers().remove(playerEntity.get());
        playerEntity.get().getLeagues().remove(leagueEntity.get());
        log.info("Finish the process of disassociating the player with ID {0} from the league with ID {1}.", playerId, leagueId);
    }
}
