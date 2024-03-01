package co.edu.uniandes.dse.ligaajedrez.services;

import co.edu.uniandes.dse.ligaajedrez.entities.PlayerEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.TournamentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.PlayerRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.TournamentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TournamentPlayerService {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public PlayerEntity addPlayer(Long tournamentId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the player with ID {0} to the tournament with ID {1}.", playerId, tournamentId);
        if (tournamentId == null || tournamentId == 0L || playerId == null || playerId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + tournamentId + " was not found.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        tournamentEntity.get().getPlayers().add(playerEntity.get());
        log.info("Finish the process of associating the player with ID {0} to the tournament with ID {1}.", playerId, tournamentId);
        return playerEntity.get();
    }

    @Transactional
    public List<PlayerEntity> getPlayers(Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all players of the tournament with ID {0}.", tournamentId);
        if (tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + tournamentId + " was not found.");
        }
        log.info("Finish the process of querying all players of the tournament with ID {0}.", tournamentId);
        return tournamentEntity.get().getPlayers();
    }

    @Transactional
    public PlayerEntity getPlayer(Long playerId, Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the player with ID {0} in the tournament with ID {1}.", playerId, tournamentId);
        if (playerId == null || playerId == 0L || tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + tournamentId + " was not found.");
        }
        if (!tournamentEntity.get().getPlayers().contains(playerEntity.get())) {
            throw new IllegalOperationException("The player is not associated with the tournament.");
        }
        log.info("Finish the process of querying the player with ID {0} in the tournament with ID {1}.", playerId, tournamentId);
        return playerEntity.get();
    }

    @Transactional
    public List<PlayerEntity> replacePlayers(Long tournamentId, List<PlayerEntity> players) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the players associated with the tournament with ID = {0}.", tournamentId);
        if (tournamentId == null || tournamentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + tournamentId + " was not found.");
        }
        for (PlayerEntity player : players) {
            Optional<PlayerEntity> playerEntity = playerRepository.findById(player.getId());
            if (playerEntity.isEmpty()) {
                throw new EntityNotFoundException("The player with ID = " + player.getId() + " was not found.");
            }
            if (!playerEntity.get().getTournaments().contains(tournamentEntity.get())) {
                playerEntity.get().getTournaments().add(tournamentEntity.get());
            }
        }
        tournamentEntity.get().setPlayers(players);
        log.info("Finish the process of replacing the players associated with the tournament with ID = {0}.", tournamentId);
        return tournamentEntity.get().getPlayers();
    }

    @Transactional
    public void removePlayer(Long tournamentId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the player with ID {0} from the tournament with ID {1}.", playerId, tournamentId);
        if (tournamentId == null || tournamentId == 0L || playerId == null || playerId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournamentId);
        if (tournamentEntity.isEmpty()) {
            throw new EntityNotFoundException("The tournament with ID = " + tournamentId + " was not found.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        if (!tournamentEntity.get().getPlayers().contains(playerEntity.get())) {
            throw new IllegalOperationException("The player is not associated with the tournament.");
        }
        tournamentEntity.get().getPlayers().remove(playerEntity.get());
        playerEntity.get().getTournaments().remove(tournamentEntity.get());
        log.info("Finish the process of disassociating the player with ID {0} from the tournament with ID {1}.", playerId, tournamentId);
    }
}
