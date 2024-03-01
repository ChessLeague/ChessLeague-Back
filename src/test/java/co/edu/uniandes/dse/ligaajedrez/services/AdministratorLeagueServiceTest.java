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

import co.edu.uniandes.dse.ligaajedrez.entities.AdministratorEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.LeagueEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({AdministratorLeagueService.class, LeagueAdministratorService.class})
public class AdministratorLeagueServiceTest {
    @Autowired
    private AdministratorLeagueService administratorLeagueService;

    @Autowired
    private LeagueAdministratorService leagueAdministratorService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private AdministratorEntity administrator = new AdministratorEntity();
    private List<LeagueEntity> leagues = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AdministratorEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from LeagueEntity").executeUpdate();
    }

    private void insertData() {
        administrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
        administrator.setIDNumber("123");
        entityManager.persist(administrator);
        for (int i = 0; i < 3; i++) {
            LeagueEntity league = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(league);
            league.getAdministrators().add(administrator);
            administrator.getLeagues().add(league);
            leagues.add(league);
        }
    }
    
    @Test
    void testAddLeague() throws IllegalOperationException, EntityNotFoundException {
        AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
        newAdministrator.setIDNumber("321");
        entityManager.persist(newAdministrator);
        LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
        entityManager.persist(newLeague);
        administratorLeagueService.addLeague(newLeague.getId(), newAdministrator.getId());
        leagueAdministratorService.addAdministrator(newAdministrator.getId(), newLeague.getId());
        LeagueEntity entity = administratorLeagueService.getLeague(newLeague.getId(), newAdministrator.getId());
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
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIDNumber("321");
            entityManager.persist(newAdministrator);
            administratorLeagueService.addLeague(null, newAdministrator.getId());
        });
    }

    @Test
    void testAddInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            administratorLeagueService.addLeague(newLeague.getId(), null);
        });
    }

    @Test
    void testAddInvalidLeague3() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIDNumber("321");
            entityManager.persist(newAdministrator);
            administratorLeagueService.addLeague(0L, newAdministrator.getId());
        });
    }

    @Test
    void testAddInvalidLeague4() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            administratorLeagueService.addLeague(newLeague.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidLeague5() {
        assertThrows(EntityNotFoundException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIDNumber("321");
            entityManager.persist(newAdministrator);
            administratorLeagueService.addLeague(321L, newAdministrator.getId());
        });
    }

    @Test
    void testAddInvalidLeague6() {
        assertThrows(EntityNotFoundException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            administratorLeagueService.addLeague(newLeague.getId(), 321L);
        });
    }

    @Test
    void testGetLeagues() throws IllegalOperationException, EntityNotFoundException {
        List<LeagueEntity> leagueEntities = administratorLeagueService.getLeagues(administrator.getId());
        assertEquals(leagueEntities.size(), leagues.size());
        for (LeagueEntity league : leagueEntities) {
            assertTrue(administrator.getLeagues().contains(league));
        }
    }

    @Test
    void testGetInvalidLegaues1() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorLeagueService.getLeagues(null);
        });
    }

    @Test
    void testGetInvalidLegaues2() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorLeagueService.getLeagues(0L);
        });
    }

    @Test
    void testGetInvalidLegaues3() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorLeagueService.getLeagues(321L);
        });
    }

    @Test
    void testGetLeague() throws IllegalOperationException, EntityNotFoundException {
        LeagueEntity league = leagues.get(0);
        LeagueEntity entity = administratorLeagueService.getLeague(league.getId(), administrator.getId());
        assertNotNull(entity);
        assertEquals(entity.getId(), league.getId());
        assertEquals(entity.getName(), league.getName());
        assertEquals(entity.getCity(), league.getCity());
        assertEquals(entity.getAddress(), league.getAddress());
        assertEquals(entity.getPhone(), league.getPhone());
        assertEquals(entity.getWebURL(), league.getWebURL());
    }

    @Test
    void testGetInvalidLegaue1() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorLeagueService.getLeague(null, administrator.getId());
        });
    }

    @Test
    void testGetInvalidLegaue2() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity league = leagues.get(0);
            administratorLeagueService.getLeague(league.getId(), null);
        });
    }

    @Test
    void testGetInvalidLegaue3() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorLeagueService.getLeague(0L, administrator.getId());
        });
    }

    @Test
    void testGetInvalidLegaue4() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity league = leagues.get(0);
            administratorLeagueService.getLeague(league.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidLegaue5() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorLeagueService.getLeague(321L, administrator.getId());
        });
    }

    @Test
    void testGetInvalidLegaue6() {
        assertThrows(EntityNotFoundException.class, () -> {
            LeagueEntity league = leagues.get(0);
            administratorLeagueService.getLeague(league.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidLegaue7() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            administratorLeagueService.getLeague(newLeague.getId(), administrator.getId());
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
        administratorLeagueService.replaceLeagues(administrator.getId(), newLeagues);
        List<LeagueEntity> leagueEntities = administratorLeagueService.getLeagues(administrator.getId());
        assertEquals(leagueEntities.size(), leagues.size());
        for (LeagueEntity league : leagueEntities) {
            assertTrue(administrator.getLeagues().contains(league));
        }
    }

    @Test
    void testReplaceInvalidLeagues1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<LeagueEntity> newLeagues = new ArrayList<>();
            administratorLeagueService.replaceLeagues(null, newLeagues);
        });
    }

    @Test
    void testReplaceInvalidLeagues2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<LeagueEntity> newLeagues = new ArrayList<>();
            administratorLeagueService.replaceLeagues(0L, newLeagues);
        });
    }

    @Test
    void testReplaceInvalidLeagues3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<LeagueEntity> newLeagues = new ArrayList<>();
            administratorLeagueService.replaceLeagues(321L, newLeagues);
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
            administratorLeagueService.replaceLeagues(administrator.getId(), newLeagues);
        });
    }

    @Test
    void testRemoveLeague() throws IllegalOperationException, EntityNotFoundException {
        for (LeagueEntity league : leagues) {
            administratorLeagueService.removeLeague(league.getId(), administrator.getId());
        }
        assertTrue(administratorLeagueService.getLeagues(administrator.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorLeagueService.removeLeague(null, administrator.getId());
        });
    }

    @Test
    void testRemoveInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorLeagueService.removeLeague(0L, administrator.getId());
        });
    }

    @Test
    void testRemoveInvalidLeague3() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            administratorLeagueService.removeLeague(newLeague.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidLeague4() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            administratorLeagueService.removeLeague(newLeague.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidLeague5() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorLeagueService.removeLeague(321L, administrator.getId());
        });
    }

    @Test
    void testRemoveInvalidLeague6() {
        assertThrows(EntityNotFoundException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            administratorLeagueService.removeLeague(newLeague.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidLeague7() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            administratorLeagueService.removeLeague(newLeague.getId(), administrator.getId());
        });
    }
}
