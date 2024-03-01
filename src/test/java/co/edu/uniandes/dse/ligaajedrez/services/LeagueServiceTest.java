package co.edu.uniandes.dse.ligaajedrez.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(LeagueService.class)
public class LeagueServiceTest {
    @Autowired
    private LeagueService leagueService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<LeagueEntity> leagueList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from LeagueEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            LeagueEntity leagueEntity = factory.manufacturePojo(LeagueEntity.class);
            leagueEntity.setName("League " + i);
            leagueEntity.setCity("City Test");
            leagueEntity.setAddress("Address Test");
            leagueEntity.setPhone("Phone Test");
            leagueEntity.setWebURL("Web URL Test");
            entityManager.persist(leagueEntity);
            leagueList.add(leagueEntity);
        }
    }

    @Test
    void testCreateLeague() throws IllegalOperationException {
        LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
        newLeague.setId(123L);
        newLeague.setName("Test League");
        newLeague.setCity("City Test");
        newLeague.setAddress("Address Test");
        newLeague.setPhone("Phone Test");
        newLeague.setWebURL("Web URL Test");
        LeagueEntity leagueEntity = leagueService.createLeague(newLeague);
        assertNotNull(leagueEntity);
        LeagueEntity entity = entityManager.find(LeagueEntity.class, leagueEntity.getId());
        assertEquals(leagueEntity.getId(), entity.getId());
        assertEquals(leagueEntity.getName(), entity.getName());
        assertEquals(leagueEntity.getCity(), entity.getCity());
        assertEquals(leagueEntity.getAddress(), entity.getAddress());
        assertEquals(leagueEntity.getPhone(), entity.getPhone());
        assertEquals(leagueEntity.getWebURL(), entity.getWebURL());
    }

    @Test
    void testCreateInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            newLeague.setId(null);
            newLeague.setName("Test League");
            leagueService.createLeague(newLeague);
        });
    }

    @Test
    void testCreateInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            newLeague.setId(0L);
            newLeague.setName("Test League");
            leagueService.createLeague(newLeague);
        });
    }

    @Test
    void testCreateInvalidLeague3() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            newLeague.setId(123L);
            newLeague.setName(null);
            leagueService.createLeague(newLeague);
        });
    }

    @Test
    void testCreateInvalidLeague4() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity newLeague = factory.manufacturePojoWithFullData(LeagueEntity.class);
            newLeague.setId(123L);
            newLeague.setName(leagueList.get(0).getName());
            leagueService.createLeague(newLeague);
        });
    }

    @Test
    void testGetLeagues() {
        List<LeagueEntity> leagues = leagueService.getLeagues();
        assertEquals(leagues.size(), leagueList.size());
        for (LeagueEntity league : leagues) {
            boolean found = false;
            for (LeagueEntity storedLeague : leagueList) {
                if (league.getId().equals(storedLeague.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void testGetLeague() throws IllegalOperationException, EntityNotFoundException {
        LeagueEntity storedLeague = leagueList.get(0);
        LeagueEntity league = leagueService.getLeague(storedLeague.getId());
        assertNotNull(league);
        assertEquals(storedLeague.getId(), league.getId());
        assertEquals(storedLeague.getName(), league.getName());
        assertEquals(storedLeague.getCity(), league.getCity());
        assertEquals(storedLeague.getAddress(), league.getAddress());
        assertEquals(storedLeague.getPhone(), league.getPhone());
        assertEquals(storedLeague.getWebURL(), league.getWebURL());
    }

    @Test
    void testGetInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueService.getLeague(null);
        });
    }

    @Test
    void testGetInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueService.getLeague(0L);
        });
    }

    @Test
    void testGetInvalidLeague3() {
        assertThrows(EntityNotFoundException.class, () -> {
            leagueService.getLeague(321L);
        });
    }

    @Test
    void testUpdateLeague() throws IllegalOperationException, EntityNotFoundException {
        LeagueEntity storedLeague = leagueList.get(0);
        LeagueEntity league = factory.manufacturePojoWithFullData(LeagueEntity.class);
        league.setId(storedLeague.getId());
        league.setName("Test League");
        league.setCity("City Test");
        league.setAddress("Address Test");
        league.setPhone("Phone Test");
        league.setWebURL("Web URL Test");
        LeagueEntity updatedLeague = leagueService.updateLeague(storedLeague.getId(), league);
        assertEquals(league.getId(), updatedLeague.getId());
        assertEquals(league.getName(), updatedLeague.getName());
        assertEquals(storedLeague.getCity(), league.getCity());
        assertEquals(storedLeague.getAddress(), league.getAddress());
        assertEquals(storedLeague.getPhone(), league.getPhone());
        assertEquals(storedLeague.getWebURL(), league.getWebURL());
    }

    @Test
    void testUpdateInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity storedLeague = leagueList.get(0);
            leagueService.updateLeague(null, storedLeague);
        });
    }

    @Test
    void testUpdateInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity storedLeague = leagueList.get(0);
            leagueService.updateLeague(0L, storedLeague);
        });
    }

    @Test
    void testUpdateInvalidLeague3() {
        assertThrows(EntityNotFoundException.class, () -> {
            leagueService.updateLeague(321L, leagueList.get(0));
        });
    }

    @Test
    void testUpdateInvalidLeague4() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity storedLeague = leagueList.get(0);
            storedLeague.setName(null);
            leagueService.updateLeague(storedLeague.getId(), storedLeague);
        });
    }

    @Test
    void testUpdateInvalidLeague5() {
        assertThrows(IllegalOperationException.class, () -> {
            LeagueEntity storedLeague = leagueList.get(0);
            storedLeague.setName(leagueList.get(1).getName());
            leagueService.updateLeague(storedLeague.getId(), storedLeague);
        });
    }

    @Test
    void testDeleteLeague() throws IllegalOperationException, EntityNotFoundException {
        LeagueEntity storedLeague = leagueList.get(0);
        leagueService.deleteLeague(storedLeague.getId());
        LeagueEntity deletedLeague = entityManager.find(LeagueEntity.class, storedLeague.getId());
        assertNull(deletedLeague);
    }

    @Test
    void testDeleteInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueService.deleteLeague(null);
        });
    }

    @Test
    void testDeleteInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueService.deleteLeague(0L);
        });
    }

    @Test
    void testDeleteInvalidLeague3() {
        assertThrows(EntityNotFoundException.class, () -> {
            leagueService.deleteLeague(321L);
        });
    }
}
