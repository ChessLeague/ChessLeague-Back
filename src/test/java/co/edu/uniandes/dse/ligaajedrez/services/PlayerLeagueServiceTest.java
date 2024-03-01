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
@Import({PlayerLeagueService.class, LeaguePlayerService.class})
public class PlayerLeagueServiceTest {
    @Autowired
    private PlayerLeagueService playerLeagueService;

    @Autowired
    private LeaguePlayerService leaguePlayerService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private PlayerEntity player = new PlayerEntity();
    private List<LeagueEntity> leagues = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PlayerEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from LeagueEntity").executeUpdate();
    }

    private void insertData() {
        player = factory.manufacturePojoWithFullData(PlayerEntity.class);
        entityManager.persist(player);
        for (int i = 0; i < 3; i++) {
            LeagueEntity league = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(league);
            league.getPlayers().add(player);
            player.getLeagues().add(league);
            leagues.add(league);
        }
    }

    @Test
    void testAddLeague() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
        entityManager.persist(newPlayer);
        LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
        entityManager.persist(newLeague);
        playerLeagueService.addLeague(newLeague.getId(), newPlayer.getId());
        leaguePlayerService.addPlayer(newLeague.getId(), newPlayer.getId());
        LeagueEntity entity = playerLeagueService.getLeague(newLeague.getId(), newPlayer.getId());
        assertEquals(newLeague.getId(), entity.getId());
        assertEquals(newLeague.getName(), entity.getName());
        assertEquals(newLeague.getCity(), entity.getCity());
        assertEquals(newLeague.getAddress(), entity.getAddress());
        assertEquals(newLeague.getPhone(), entity.getPhone());
        assertEquals(newLeague.getWebURL(), entity.getWebURL());
    }

    @Test
    void testAddInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            playerLeagueService.addLeague(null, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.addLeague(newLeague.getId(), null);
        });
    }

    @Test
    void testAddInvalidLeague3() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            playerLeagueService.addLeague(0L, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidLeague4() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.addLeague(newLeague.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidLeague5() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            playerLeagueService.addLeague(321L, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidLeague6() {
        assertThrows(EntityNotFoundException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.addLeague(newLeague.getId(), 321L);
        });
    }

    @Test
    void testGetLeagues() throws IllegalOperationException, EntityNotFoundException {
        List<LeagueEntity> leagueEntities = playerLeagueService.getLeagues(player.getId());
        assertEquals(leagueEntities.size(), leagues.size());
        for (LeagueEntity league : leagueEntities) {
            assertTrue(player.getLeagues().contains(league));
        }
    }

    @Test
    void testGetInvalidLeagues1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerLeagueService.getLeagues(null);
        });
    }

    @Test
    void testGetInvalidLeagues2() {
        assertThrows(IllegalOperationException.class, () -> {
            playerLeagueService.getLeagues(0L);
        });
    }

    @Test
    void testGetInvalidLeagues3() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerLeagueService.getLeagues(321L);
        });
    }

    @Test
    void testGetLeague() throws IllegalOperationException, EntityNotFoundException {
        LeagueEntity league = leagues.get(0);
        LeagueEntity entity = playerLeagueService.getLeague(league.getId(), player.getId());
        assertNotNull(entity);
        assertEquals(entity.getId(), league.getId());
        assertEquals(entity.getName(), league.getName());
        assertEquals(entity.getCity(), league.getCity());
        assertEquals(entity.getAddress(), league.getAddress());
        assertEquals(entity.getPhone(), league.getPhone());
        assertEquals(entity.getWebURL(), league.getWebURL());
    }

    @Test
    void testGetInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerLeagueService.getLeague(null, player.getId());
        });
    }

    @Test
    void testGetInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity league = leagues.get(0);
            playerLeagueService.getLeague(league.getId(), null);
        });
    }

    @Test
    void testGetInvalidLeague3() {
        assertThrows(IllegalOperationException.class, () -> {
            playerLeagueService.getLeague(0L, player.getId());
        });
    }

    @Test
    void testGetInvalidLeague4() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity league = leagues.get(0);
            playerLeagueService.getLeague(league.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidLeague5() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerLeagueService.getLeague(321L, player.getId());
        });
    }

    @Test
    void testGetInvalidLeague6() {
        assertThrows(EntityNotFoundException.class, () -> {
            LeagueEntity league = leagues.get(0);
            playerLeagueService.getLeague(league.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidLeague7() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.getLeague(newLeague.getId(), player.getId());
        });
    }

    @Test
    void testReplaceLeagues() throws IllegalOperationException, EntityNotFoundException {
        List<LeagueEntity> newLeagues = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            LeagueEntity league = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(league);
            newLeagues.add(league);
        }
        playerLeagueService.replaceLeagues(player.getId(), newLeagues);
        List<LeagueEntity> leagueEntities = playerLeagueService.getLeagues(player.getId());
        assertEquals(leagueEntities.size(), leagues.size());
        for (LeagueEntity league : leagueEntities) {
            assertTrue(player.getLeagues().contains(league));
        }
    }

    @Test
    void testReplaceInvalidLeagues1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<LeagueEntity> newLeagues = new ArrayList<>();
            playerLeagueService.replaceLeagues(null, newLeagues);
        });
    }

    @Test
    void testReplaceInvalidLeagues2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<LeagueEntity> newLeagues = new ArrayList<>();
            playerLeagueService.replaceLeagues(0L, newLeagues);
        });
    }

    @Test
    void testReplaceInvalidLeagues3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<LeagueEntity> newLeagues = new ArrayList<>();
            playerLeagueService.replaceLeagues(321L, newLeagues);
        });
    }

    @Test
    void testReplaceInvalidLeagues4() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<LeagueEntity> newLeagues = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                LeagueEntity league = factory.manufacturePojoWithFullData(LeagueEntity.class);
                entityManager.persist(league);
                newLeagues.add(league);
            }
            LeagueEntity invalidLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            invalidLeague.setId(321L);
            newLeagues.add(invalidLeague);
            playerLeagueService.replaceLeagues(player.getId(), newLeagues);
        });
    }

    @Test
    void testRemoveLeague() throws IllegalOperationException, EntityNotFoundException {
        for (LeagueEntity league : leagues) {
            playerLeagueService.removeLeague(league.getId(), player.getId());
        }
        assertTrue(playerLeagueService.getLeagues(player.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerLeagueService.removeLeague(null, player.getId());
        });
    }

    @Test
    void testRemoveInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            playerLeagueService.removeLeague(0L, player.getId());
        });
    }

    @Test
    void testRemoveInvalidLeague3() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.removeLeague(newLeague.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidLeague4() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.removeLeague(newLeague.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidLeague5() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerLeagueService.removeLeague(321L, player.getId());
        });
    }

    @Test
    void testRemoveInvalidLeague6() {
        assertThrows(EntityNotFoundException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.removeLeague(newLeague.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidLeague7() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            playerLeagueService.removeLeague(newLeague.getId(), player.getId());
        });
    }
}
