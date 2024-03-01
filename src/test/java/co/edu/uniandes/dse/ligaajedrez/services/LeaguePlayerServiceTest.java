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

import co.edu.uniandes.dse.ligaajedrez.entities.LeagueEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.PlayerEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({LeaguePlayerService.class, PlayerLeagueService.class})
public class LeaguePlayerServiceTest {
    @Autowired
    private LeaguePlayerService leaguePlayerService;

    @Autowired
    private PlayerLeagueService playerLeagueService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private LeagueEntity league = new LeagueEntity();
    private List<PlayerEntity> players = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from LeagueEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PlayerEntity").executeUpdate();
    }

    private void insertData() {
        league = factory.manufacturePojoWithFullData(LeagueEntity.class);
        entityManager.persist(league);
        for (int i = 0; i < 3; i++) {
            PlayerEntity player = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(player);
            player.getLeagues().add(league);
            league.getPlayers().add(player);
            players.add(player);
        }
    }

    @Test
    void testAddPlayerLeague() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
        entityManager.persist(newPlayer);
        LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
        entityManager.persist(newLeague);
        leaguePlayerService.addPlayer(newLeague.getId(), newPlayer.getId());
        playerLeagueService.addLeague(newLeague.getId(), newPlayer.getId());
        PlayerEntity entity = leaguePlayerService.getPlayer(newPlayer.getId(), newLeague.getId());
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
    void testAddInvalidPlayerLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            leaguePlayerService.addPlayer(null, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidPlayerLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.addLeague(newLeague.getId(), null);
        });
    }

    @Test
    void testAddInvalidPlayerLeague3() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            leaguePlayerService.addPlayer(0L, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidPlayerLeague4() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.addLeague(newLeague.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidPlayerLeague5() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            leaguePlayerService.addPlayer(321L, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidPlayerLeague6() {
        assertThrows(EntityNotFoundException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.addLeague(newLeague.getId(), 321L);
        });
    }

    @Test
    void testGetPlayers() throws IllegalOperationException, EntityNotFoundException {
        List<PlayerEntity> playerEntities = leaguePlayerService.getPlayers(league.getId());
        assertEquals(playerEntities.size(), players.size());
        for (PlayerEntity player : playerEntities) {
            assertTrue(league.getPlayers().contains(player));
        }
    }

    @Test
    void testGetInvalidPlayers1() {
        assertThrows(IllegalOperationException.class, () -> {
            leaguePlayerService.getPlayers(null);
        });
    }

    @Test
    void testGetInvalidPlayers2() {
        assertThrows(IllegalOperationException.class, () -> {
            leaguePlayerService.getPlayers(0L);
        });
    }

    @Test
    void testGetInvalidPlayers3() {
        assertThrows(EntityNotFoundException.class, () -> {
            leaguePlayerService.getPlayers(321L);
        });
    }

    @Test
    void testGetPlayer() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity player = players.get(0);
        PlayerEntity entity = leaguePlayerService.getPlayer(player.getId(), league.getId());
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
            leaguePlayerService.getPlayer(null, league.getId());
        });
    }

    @Test
    void testGetInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity player = players.get(0);
            leaguePlayerService.getPlayer(player.getId(), null);
        });
    }

    @Test
    void testGetInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            leaguePlayerService.getPlayer(0L, league.getId());
        });
    }

    @Test
    void testGetInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity player = players.get(0);
            leaguePlayerService.getPlayer(player.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            leaguePlayerService.getPlayer(321L, league.getId());
        });
    }

    @Test
    void testGetInvalidPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity player = players.get(0);
            leaguePlayerService.getPlayer(player.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidPlayer7() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            leaguePlayerService.getPlayer(newPlayer.getId(), league.getId());
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
        leaguePlayerService.replacePlayers(league.getId(), newPlayers);
        List<PlayerEntity> playerEntities = leaguePlayerService.getPlayers(league.getId());
        assertEquals(playerEntities.size(), players.size());
        for (PlayerEntity player : playerEntities) {
            assertTrue(league.getPlayers().contains(player));
        }
    }

    @Test
    void testReplaceInvalidPlayers1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            leaguePlayerService.replacePlayers(null, newPlayers);
        });
    }

    @Test
    void testReplaceInvalidPlayers2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            leaguePlayerService.replacePlayers(0L, newPlayers);
        });
    }

    @Test
    void testReplaceInvalidPlayers3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            leaguePlayerService.replacePlayers(321L, newPlayers);
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
            leaguePlayerService.replacePlayers(league.getId(), newPlayers);
        });
    }

    @Test
    void testRemovePlayer() throws IllegalOperationException, EntityNotFoundException {
        for (PlayerEntity player : players) {
            leaguePlayerService.removePlayer(league.getId(), player.getId());
        }
        assertTrue(leaguePlayerService.getPlayers(league.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            leaguePlayerService.removePlayer(null, league.getId());
        });
    }

    @Test
    void testRemoveInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            leaguePlayerService.removePlayer(0L, league.getId());
        });
    }

    @Test
    void testRemoveInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            leaguePlayerService.removePlayer(newPlayer.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            leaguePlayerService.removePlayer(newPlayer.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            leaguePlayerService.removePlayer(321L, league.getId());
        });
    }

    @Test
    void testRemoveInvalidPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            leaguePlayerService.removePlayer(newPlayer.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidPlayer7() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            leaguePlayerService.removePlayer(league.getId(), newPlayer.getId());
        });
    }
}
