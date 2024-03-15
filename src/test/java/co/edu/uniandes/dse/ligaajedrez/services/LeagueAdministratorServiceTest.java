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
import co.edu.uniandes.dse.ligaajedrez.entities.AdministratorEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({LeagueAdministratorService.class, AdministratorLeagueService.class})
public class LeagueAdministratorServiceTest {
    @Autowired
    private LeagueAdministratorService leagueAdministratorService;

    @Autowired
    private AdministratorLeagueService administratorLeagueService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private LeagueEntity league = new LeagueEntity();
    private List<AdministratorEntity> administrators = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from LeagueEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdministratorEntity").executeUpdate();
    }

    private void insertData() {
        league = factory.manufacturePojoWithFullData(LeagueEntity.class);
        entityManager.persist(league);
        for (int i = 0; i < 3; i++) {
            AdministratorEntity administrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            administrator.setIdNumber(Integer.toString(i));
            entityManager.persist(administrator);
            administrator.getLeagues().add(league);
            league.getAdministrators().add(administrator);
            administrators.add(administrator);
        }
    }

    @Test
    void testAddAdministrator() throws IllegalOperationException, EntityNotFoundException {
        LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
        entityManager.persist(newLeague);
        AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
        newAdministrator.setIdNumber("321");
        entityManager.persist(newAdministrator);
        leagueAdministratorService.addAdministrator(newAdministrator.getId(), newLeague.getId());
        administratorLeagueService.addLeague(newLeague.getId(), newAdministrator.getId());
        AdministratorEntity entity = leagueAdministratorService.getAdministrator(newAdministrator.getId(), newLeague.getId());
        assertEquals(newAdministrator.getId(), entity.getId());
        assertEquals(newAdministrator.getName(), entity.getName());
        assertEquals(newAdministrator.getPhotoURL(), entity.getPhotoURL());
        assertEquals(newAdministrator.getIdNumber(), entity.getIdNumber());
        assertEquals(newAdministrator.getUsername(), entity.getUsername());
        assertEquals(newAdministrator.getPassword(), entity.getPassword());
    }

    @Test
    void testAddInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            leagueAdministratorService.addAdministrator(null, newLeague.getId());
        });
    }

    @Test
    void testAddInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIdNumber("321");
            entityManager.persist(newAdministrator);
            leagueAdministratorService.addAdministrator(newAdministrator.getId(), null);
        });
    }

    @Test
    void testAddInvalidAdministrator3() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            leagueAdministratorService.addAdministrator(0L, newLeague.getId());
        });
    }

    @Test
    void testAddInvalidAdministrator4() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIdNumber("321");
            entityManager.persist(newAdministrator);
            leagueAdministratorService.addAdministrator(newAdministrator.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidAdministrator5() {
        assertThrows(EntityNotFoundException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(newLeague);
            leagueAdministratorService.addAdministrator(321L, newLeague.getId());
        });
    }

    @Test
    void testAddInvalidAdministrator6() {
        assertThrows(EntityNotFoundException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIdNumber("321");
            entityManager.persist(newAdministrator);
            leagueAdministratorService.addAdministrator(newAdministrator.getId(), 321L);
        });
    }

    @Test
    void testGetAdministrators() throws IllegalOperationException, EntityNotFoundException {
        List<AdministratorEntity> administratorEntities = leagueAdministratorService.getAdministrators(league.getId());
        assertEquals(administratorEntities.size(), administrators.size());
        for (AdministratorEntity administrator : administratorEntities) {
            assertTrue(league.getAdministrators().contains(administrator));
        }
    }

    @Test
    void testGetInvalidAdministrators1() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueAdministratorService.getAdministrators(null);
        });
    }

    @Test
    void testGetInvalidAdministrators2() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueAdministratorService.getAdministrators(0L);
        });
    }

    @Test
    void testGetInvalidAdministrators3() {
        assertThrows(EntityNotFoundException.class, () -> {
            leagueAdministratorService.getAdministrators(321L);
        });
    }

    @Test
    void testGetAdministrator() throws IllegalOperationException, EntityNotFoundException {
        AdministratorEntity administrator = administrators.get(0);
        AdministratorEntity entity = leagueAdministratorService.getAdministrator(administrator.getId(), league.getId());
        assertNotNull(entity);
        assertEquals(entity.getId(), administrator.getId());
        assertEquals(entity.getName(), administrator.getName());
        assertEquals(entity.getPhotoURL(), administrator.getPhotoURL());
        assertEquals(entity.getIdNumber(), administrator.getIdNumber());
        assertEquals(entity.getUsername(), administrator.getUsername());
        assertEquals(entity.getPassword(), administrator.getPassword());
    }

    @Test
    void testGetInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueAdministratorService.getAdministrator(null, league.getId());
        });
    }

    @Test
    void testGetInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity administrator = administrators.get(0);
            leagueAdministratorService.getAdministrator(administrator.getId(), null);
        });
    }

    @Test
    void testGetInvalidAdministrator3() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueAdministratorService.getAdministrator(0L, league.getId());
        });
    }

    @Test
    void testGetInvalidAdministrator4() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity administrator = administrators.get(0);
            leagueAdministratorService.getAdministrator(administrator.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidAdministrator5() {
        assertThrows(EntityNotFoundException.class, () -> {
            leagueAdministratorService.getAdministrator(321L, league.getId());
        });
    }

    @Test
    void testGetInvalidAdministrator6() {
        assertThrows(EntityNotFoundException.class, () -> {
            AdministratorEntity administrator = administrators.get(0);
            leagueAdministratorService.getAdministrator(administrator.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidAdministrator7() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIdNumber("123");
            entityManager.persist(newAdministrator);
            leagueAdministratorService.getAdministrator(newAdministrator.getId(), league.getId());
        });
    }

    @Test
    void testReplaceAdministrators() throws IllegalOperationException, EntityNotFoundException {
        List<AdministratorEntity> newAdministrators = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            AdministratorEntity administrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            administrator.setIdNumber(Integer.toString(i) + "0");
            entityManager.persist(administrator);
            newAdministrators.add(administrator);
        }
        leagueAdministratorService.replaceAdministrators(league.getId(), newAdministrators);
        List<AdministratorEntity> administratorEntities = leagueAdministratorService.getAdministrators(league.getId());
        assertEquals(administratorEntities.size(), administrators.size());
        for (AdministratorEntity administrator : administratorEntities) {
            assertTrue(league.getAdministrators().contains(administrator));
        }
    }

    @Test
    void testReplaceInvalidAdministrators1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<AdministratorEntity> newAdministrators = new ArrayList<>();
            leagueAdministratorService.replaceAdministrators(null, newAdministrators);
        });
    }

    @Test
    void testReplaceInvalidAdministrators2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<AdministratorEntity> newAdministrators = new ArrayList<>();
            leagueAdministratorService.replaceAdministrators(0L, newAdministrators);
        });
    }

    @Test
    void testReplaceInvalidAdministrators3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<AdministratorEntity> newAdministrators = new ArrayList<>();
            leagueAdministratorService.replaceAdministrators(321L, newAdministrators);
        });
    }

    @Test
    void testReplaceInvalidAdministrators4() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<AdministratorEntity> newAdministrators = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                AdministratorEntity administrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
                administrator.setIdNumber(Integer.toString(i) + "0");
                entityManager.persist(administrator);
                newAdministrators.add(administrator);
            }
            AdministratorEntity invalidAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            invalidAdministrator.setId(321L);
            newAdministrators.add(invalidAdministrator);
            leagueAdministratorService.replaceAdministrators(league.getId(), newAdministrators);
        });
    }

    @Test
    void testRemoveAdministrator() throws IllegalOperationException, EntityNotFoundException {
        for (AdministratorEntity administrator : administrators) {
            leagueAdministratorService.removeAdministrator(administrator.getId(), league.getId());
        }
        assertTrue(leagueAdministratorService.getAdministrators(league.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueAdministratorService.removeAdministrator(null, league.getId());
        });
    }

    @Test
    void testRemoveInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueAdministratorService.removeAdministrator(0L, league.getId());
        });
    }

    @Test
    void testRemoveInvalidAdministrator3() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIdNumber("123");
            entityManager.persist(newAdministrator);
            leagueAdministratorService.removeAdministrator(newAdministrator.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidAdministrator4() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIdNumber("123");
            entityManager.persist(newAdministrator);
            leagueAdministratorService.removeAdministrator(newAdministrator.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidAdministrator5() {
        assertThrows(EntityNotFoundException.class, () -> {
            leagueAdministratorService.removeAdministrator(321L, league.getId());
        });
    }

    @Test
    void testRemoveInvalidAdministrator6() {
        assertThrows(EntityNotFoundException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIdNumber("123");
            entityManager.persist(newAdministrator);
            leagueAdministratorService.removeAdministrator(newAdministrator.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidAdministrator7() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setIdNumber("123");
            entityManager.persist(newAdministrator);
            leagueAdministratorService.removeAdministrator(newAdministrator.getId(), league.getId());
        });
    }
}
