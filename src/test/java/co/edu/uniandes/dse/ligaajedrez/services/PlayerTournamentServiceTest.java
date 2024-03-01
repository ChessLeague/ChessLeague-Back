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
@Import({PlayerTournamentService.class, TournamentPlayerService.class})
public class PlayerTournamentServiceTest {
    @Autowired
    private PlayerTournamentService playerTournamentService;

    @Autowired
    private TournamentPlayerService tournamentPlayerService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private PlayerEntity player = new PlayerEntity();
    private List<TournamentEntity> tournaments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PlayerEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from TournamentEntity").executeUpdate();
    }

    private void insertData() {
        player = factory.manufacturePojoWithFullData(PlayerEntity.class);
        entityManager.persist(player);
        for (int i = 0; i < 3; i++) {
            TournamentEntity tournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(tournament);
            tournament.getPlayers().add(player);
            player.getTournaments().add(tournament);
            tournaments.add(tournament);
        }
    }

    @Test
    void testAddTournamentPlayer() throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
        entityManager.persist(newTournament);
        PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
        entityManager.persist(newPlayer);
        playerTournamentService.addTournament(newPlayer.getId(), newTournament.getId());
        tournamentPlayerService.addPlayer(newTournament.getId(), newPlayer.getId());
        TournamentEntity storedTournament = playerTournamentService.getTournament(newTournament.getId(), newPlayer.getId());
        assertEquals(storedTournament.getId(), newTournament.getId());
        assertEquals(storedTournament.getName(), newTournament.getName());
        assertEquals(storedTournament.getLocation(), newTournament.getLocation());
        assertEquals(storedTournament.getDate(), newTournament.getDate());
        assertEquals(storedTournament.getPrize(), newTournament.getPrize());
        assertEquals(storedTournament.getImage(), newTournament.getImage());
    }

    @Test
    void testAddInvalidTournamentPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            playerTournamentService.addTournament(null, newTournament.getId());
        });
    }

    @Test
    void testAddInvalidTournamentPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.addPlayer(newPlayer.getId(), null);
        });
    }

    @Test
    void testAddInvalidTournamentPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            playerTournamentService.addTournament(0L, newTournament.getId());
        });
    }

    @Test
    void testAddInvalidTournamentPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.addPlayer(newPlayer.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidTournamentPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            playerTournamentService.addTournament(321L, newTournament.getId());
        });
    }

    @Test
    void testAddInvalidTournamentPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            tournamentPlayerService.addPlayer(newPlayer.getId(), 321L);
        });
    }

    @Test
    void testGetTournaments() throws IllegalOperationException, EntityNotFoundException {
        List<TournamentEntity> tournamentEntities = playerTournamentService.getTournaments(player.getId());
        assertEquals(tournamentEntities.size(), tournaments.size());
        for (TournamentEntity tournament : tournamentEntities) {
            assertTrue(player.getTournaments().contains(tournament));
        }
    }

    @Test
    void testGetInvalidTournaments1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerTournamentService.getTournaments(null);
        });
    }

    @Test
    void testGetInvalidTournaments2() {
        assertThrows(IllegalOperationException.class, () -> {
            playerTournamentService.getTournaments(0L);
        });
    }

    @Test
    void testGetInvalidTournaments3() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerTournamentService.getTournaments(321L);
        });
    }

    @Test
    void testGetTournament() throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity tournament = tournaments.get(0);
        TournamentEntity storedTournament = playerTournamentService.getTournament(tournament.getId(), player.getId());
        assertNotNull(storedTournament);
        assertEquals(storedTournament.getId(), tournament.getId());
        assertEquals(storedTournament.getName(), tournament.getName());
        assertEquals(storedTournament.getLocation(), tournament.getLocation());
        assertEquals(storedTournament.getDate(), tournament.getDate());
        assertEquals(storedTournament.getPrize(), tournament.getPrize());
        assertEquals(storedTournament.getImage(), tournament.getImage());
    }

    @Test
    void testGetInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerTournamentService.getTournament(null, player.getId());
        });
    }

    @Test
    void testGetInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity tournament = tournaments.get(0);
            playerTournamentService.getTournament(tournament.getId(), null);
        });
    }

    @Test
    void testGetInvalidTournament3() {
        assertThrows(IllegalOperationException.class, () -> {
            playerTournamentService.getTournament(0L, player.getId());
        });
    }

    @Test
    void testGetInvalidTournament4() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity tournament = tournaments.get(0);
            playerTournamentService.getTournament(tournament.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidTournament5() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerTournamentService.getTournament(321L, player.getId());
        });
    }

    @Test
    void testGetInvalidTournament6() {
        assertThrows(EntityNotFoundException.class, () -> {
            TournamentEntity tournament = tournaments.get(0);
            playerTournamentService.getTournament(tournament.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidTournament7() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            playerTournamentService.getTournament(newTournament.getId(), player.getId());
        });
    }

    @Test
    void testReplaceTournaments() throws IllegalOperationException, EntityNotFoundException {
        List<TournamentEntity> newTournaments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TournamentEntity tournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(tournament);
            newTournaments.add(tournament);
        }
        playerTournamentService.replaceTournaments(player.getId(), newTournaments);
        List<TournamentEntity> tournamentEntities = playerTournamentService.getTournaments(player.getId());
        assertEquals(tournamentEntities.size(), tournaments.size());
        for (TournamentEntity tournament : tournamentEntities) {
            assertTrue(player.getTournaments().contains(tournament));
        }
    }

    @Test
    void testReplaceInvalidTournaments1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<TournamentEntity> newTournaments = new ArrayList<>();
            playerTournamentService.replaceTournaments(null, newTournaments);
        });
    }

    @Test
    void testReplaceInvalidTournaments2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<TournamentEntity> newTournaments = new ArrayList<>();
            playerTournamentService.replaceTournaments(0L, newTournaments);
        });
    }

    @Test
    void testReplaceInvalidTournaments3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<TournamentEntity> newTournaments = new ArrayList<>();
            playerTournamentService.replaceTournaments(321L, newTournaments);
        });
    }

    @Test
    void testReplaceInvalidTournaments4() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<TournamentEntity> newTournaments = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                TournamentEntity tournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
                entityManager.persist(tournament);
                newTournaments.add(tournament);
            }
            TournamentEntity invalidTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            invalidTournament.setId(321L);
            newTournaments.add(invalidTournament);
            playerTournamentService.replaceTournaments(player.getId(), newTournaments);
        });
    }

    @Test
    void testRemoveTournament() throws IllegalOperationException, EntityNotFoundException {
        for (TournamentEntity tournament : tournaments) {
            playerTournamentService.removeTournament(player.getId(), tournament.getId());
        }
        assertTrue(playerTournamentService.getTournaments(player.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerTournamentService.removeTournament(null, tournaments.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            playerTournamentService.removeTournament(0L, tournaments.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidTournament3() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            playerTournamentService.removeTournament(player.getId(), newTournament.getId());
        });
    }
}
