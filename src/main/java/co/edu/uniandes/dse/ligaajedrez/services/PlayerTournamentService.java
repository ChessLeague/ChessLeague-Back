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
public class PlayerTournamentService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Transactional
    public TournamentEntity addTournament(Long playerId, Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the tournament with ID {0} to the player with ID {1}.", tournamentId, playerId);
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
        playerEntity.get().getTournaments().add(tournamentEntity.get());
        log.info("Finish the process of associating the tournament with ID {0} to the player with ID {1}.", tournamentId, playerId);
        return tournamentEntity.get();
    }

    @Transactional
    public List<TournamentEntity> getTournaments(Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all tournaments of the player with ID {0}.", playerId);
        if (playerId == null || playerId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        log.info("Finish the process of querying all tournaments of the player with ID {0}.", playerId);
        return playerEntity.get().getTournaments();
    }

    @Transactional
    public TournamentEntity getTournament(Long tournamentId, Long playerId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the tournament with ID {0} for the player with ID {1}.", tournamentId, playerId);
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
        if (!playerEntity.get().getTournaments().contains(tournamentEntity.get())) {
            throw new IllegalOperationException("The tournament is not associated with the player.");
        }
        log.info("Finish the process of querying the tournament with ID {0} for the player with ID {1}.", tournamentId, playerId);
        return tournamentEntity.get();
    }

    @Transactional
    public List<TournamentEntity> replaceTournaments(Long playerId, List<TournamentEntity> tournaments) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the tournaments associated with the player with ID = {0}.", playerId);
        if (playerId == null || playerId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<PlayerEntity> playerEntity = playerRepository.findById(playerId);
        if (playerEntity.isEmpty()) {
            throw new EntityNotFoundException("The player with ID = " + playerId + " was not found.");
        }
        for (TournamentEntity tournament : tournaments) {
            Optional<TournamentEntity> tournamentEntity = tournamentRepository.findById(tournament.getId());
            if (tournamentEntity.isEmpty()) {
                throw new EntityNotFoundException("The tournament with ID = " + tournament.getId() + " was not found.");
            }
            if (!tournamentEntity.get().getPlayers().contains(playerEntity.get())) {
                tournamentEntity.get().getPlayers().add(playerEntity.get());
            }
        }
        playerEntity.get().setTournaments(tournaments);
        log.info("Finish the process of replacing the tournaments associated with the player with ID = {0}.", playerId);
        return playerEntity.get().getTournaments();
    }

    @Transactional
    public void removeTournament(Long playerId, Long tournamentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the tournament with ID {0} from the player with ID {1}.", tournamentId, playerId);
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
        if (!playerEntity.get().getTournaments().contains(tournamentEntity.get())) {
            throw new IllegalOperationException("The tournament is not associated with the player.");
        }
        playerEntity.get().getTournaments().remove(tournamentEntity.get());
        tournamentEntity.get().getPlayers().remove(playerEntity.get());
        log.info("Finish the process of disassociating the tournament with ID {0} from the player with ID {1}.", tournamentId, playerId);
    }
}
