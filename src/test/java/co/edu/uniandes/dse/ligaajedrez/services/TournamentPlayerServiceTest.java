package co.edu.uniandes.dse.ligaajedrez.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.TournamentEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.PlayerEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({TournamentPlayerService.class, PlayerTournamentService.class})
public class TournamentPlayerServiceTest {
    @Autowired
    private TournamentPlayerService tournamentPlayerService;

    @Autowired
    private PlayerTournamentService playerTournamentService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private TournamentEntity tournament = new TournamentEntity();
    private List<PlayerEntity> players = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from TournamentEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PlayerEntity").executeUpdate();
    }

    private void insertData() {
        tournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
        entityManager.persist(tournament);
        for (int i = 0; i < 3; i++) {
            PlayerEntity player = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(player);
            player.getTournaments().add(tournament);
            tournament.getPlayers().add(player);
            players.add(player);
        }
    }

    @Test
    void testAddPlayer() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
        entityManager.persist(newPlayer);
        TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
        entityManager.persist(newTournament);
        tournamentPlayerService.addPlayer(newTournament.getId(), newPlayer.getId());
        playerTournamentService.addTournament(newPlayer.getId(), newTournament.getId());
        PlayerEntity entity = tournamentPlayerService.getPlayer(newPlayer.getId(), newTournament.getId());
        assertEquals(newPlayer.getId(), entity.getId());
        assertEquals(newPlayer.getName(), entity.getName());
        assertEquals(newPlayer.getPhotoURL(), entity.getPhotoURL());
        assertEquals(newPlayer.getBirthDate(), entity.getBirthDate());
        assertEquals(newPlayer.getBirthPlace(), entity.getBirthPlace());
        assertEquals(newPlayer.getEloRating(), entity.getEloRating());
        assertEquals(newPlayer.getUsername(), entity.getUsername());
        assertEquals(newPlayer.getPassword(), entity.getPassword());
    }

    @Test
    void testAddInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.addPlayer(null, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            playerTournamentService.addTournament(newTournament.getId(), null);
        });
    }

    @Test
    void testAddInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.addPlayer(0L, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            playerTournamentService.addTournament(newTournament.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.addPlayer(321L, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            playerTournamentService.addTournament(newTournament.getId(), 321L);
        });
    }

    @Test
    void testGetPlayers() throws IllegalOperationException, EntityNotFoundException {
        List<PlayerEntity> playerEntities = tournamentPlayerService.getPlayers(tournament.getId());
        assertEquals(playerEntities.size(), players.size());
        for (PlayerEntity player : playerEntities) {
            assertTrue(tournament.getPlayers().contains(player));
        }
    }

    @Test
    void testGetInvalidPlayers1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentPlayerService.getPlayers(null);
        });
    }

    @Test
    void testGetInvalidPlayers2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentPlayerService.getPlayers(0L);
        });
    }

    @Test
    void testGetInvalidPlayers3() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentPlayerService.getPlayers(321L);
        });
    }

    @Test
    void testGetPlayer() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity player = players.get(0);
        PlayerEntity entity = tournamentPlayerService.getPlayer(player.getId(), tournament.getId());
        assertNotNull(entity);
        assertEquals(player.getId(), entity.getId());
        assertEquals(player.getName(), entity.getName());
        assertEquals(player.getPhotoURL(), entity.getPhotoURL());
        assertEquals(player.getBirthDate(), entity.getBirthDate());
        assertEquals(player.getBirthPlace(), entity.getBirthPlace());
        assertEquals(player.getEloRating(), entity.getEloRating());
        assertEquals(player.getUsername(), entity.getUsername());
        assertEquals(player.getPassword(), entity.getPassword());
    }

    @Test
    void testGetInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentPlayerService.getPlayer(null, tournament.getId());
        });
    }

    @Test
    void testGetInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity player = players.get(0);
            tournamentPlayerService.getPlayer(player.getId(), null);
        });
    }

    @Test
    void testGetInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentPlayerService.getPlayer(0L, tournament.getId());
        });
    }

    @Test
    void testGetInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity player = players.get(0);
            tournamentPlayerService.getPlayer(player.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentPlayerService.getPlayer(321L, tournament.getId());
        });
    }

    @Test
    void testGetInvalidPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity player = players.get(0);
            tournamentPlayerService.getPlayer(player.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidPlayer7() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.getPlayer(newPlayer.getId(), tournament.getId());
        });
    }

    @Test
    void testReplacePlayers() throws IllegalOperationException, EntityNotFoundException {
        List<PlayerEntity> newPlayers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PlayerEntity player = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(player);
            newPlayers.add(player);
        }
        tournamentPlayerService.replacePlayers(tournament.getId(), newPlayers);
        List<PlayerEntity> playerEntities = tournamentPlayerService.getPlayers(tournament.getId());
        assertEquals(playerEntities.size(), players.size());
        for (PlayerEntity player : playerEntities) {
            assertTrue(tournament.getPlayers().contains(player));
        }
    }

    @Test
    void testReplaceInvalidPlayers1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            tournamentPlayerService.replacePlayers(null, newPlayers);
        });
    }

    @Test
    void testReplaceInvalidPlayers2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            tournamentPlayerService.replacePlayers(0L, newPlayers);
        });
    }

    @Test
    void testReplaceInvalidPlayers3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            tournamentPlayerService.replacePlayers(321L, newPlayers);
        });
    }

    @Test
    void testReplaceInvalidPlayers4() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                PlayerEntity player = factory.manufacturePojoWithFullData(PlayerEntity.class);
                entityManager.persist(player);
                newPlayers.add(player);
            }
            PlayerEntity invalidPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            invalidPlayer.setId(321L);
            newPlayers.add(invalidPlayer);
            tournamentPlayerService.replacePlayers(tournament.getId(), newPlayers);
        });
    }

    @Test
    void testRemovePlayer() throws IllegalOperationException, EntityNotFoundException {
        for (PlayerEntity player : players) {
            tournamentPlayerService.removePlayer(tournament.getId(), player.getId());
        }
        assertTrue(tournamentPlayerService.getPlayers(tournament.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentPlayerService.removePlayer(null, tournament.getId());
        });
    }

    @Test
    void testRemoveInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentPlayerService.removePlayer(0L, tournament.getId());
        });
    }

    @Test
    void testRemoveInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.removePlayer(newPlayer.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.removePlayer(newPlayer.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentPlayerService.removePlayer(321L, tournament.getId());
        });
    }

    @Test
    void testRemoveInvalidPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.removePlayer(newPlayer.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidPlayer7() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.removePlayer(tournament.getId(), newPlayer.getId());
        });
    }
}
