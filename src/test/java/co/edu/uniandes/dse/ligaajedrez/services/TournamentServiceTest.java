package co.edu.uniandes.dse.ligaajedrez.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.TournamentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(TournamentService.class)
public class TournamentServiceTest {
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<TournamentEntity> tournamentList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from TournamentEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            TournamentEntity tournamentEntity = factory.manufacturePojo(TournamentEntity.class);
            tournamentEntity.setName("Tournament Name" + i);
            tournamentEntity.setLocation("Test Location");
            tournamentEntity.setDate(new Date());
            tournamentEntity.setPrize("Test Prize");
            entityManager.persist(tournamentEntity);
            tournamentList.add(tournamentEntity);
        }
    }

    @Test
    void testCreateTournament() throws IllegalOperationException {
        TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
        newTournament.setId(123L);
        newTournament.setName("Test Name");
        newTournament.setLocation("Test Location");
        newTournament.setDate(new Date());
        newTournament.setPrize("Test Prize");
        TournamentEntity tournamentEntity = tournamentService.createTournament(newTournament);
        assertNotNull(tournamentEntity);
        TournamentEntity entity = entityManager.find(TournamentEntity.class, tournamentEntity.getId());
        assertEquals(tournamentEntity.getId(), entity.getId());
        assertEquals(tournamentEntity.getName(), entity.getName());
        assertEquals(tournamentEntity.getLocation(), entity.getLocation());
        assertEquals(tournamentEntity.getDate(), entity.getDate());
        assertEquals(tournamentEntity.getPrize(), entity.getPrize());
    }

    @Test
    void testCreateInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            newTournament.setId(null);
            newTournament.setName("Test Name");
            tournamentService.createTournament(newTournament);
        });
    }

    @Test
    void testCreateInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            newTournament.setId(0L);
            newTournament.setName("Test Name");
            tournamentService.createTournament(newTournament);
        });
    }

    @Test
    void testCreateInvalidTournament3() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            newTournament.setId(123L);
            newTournament.setName(null);
            tournamentService.createTournament(newTournament);
        });
    }

    @Test
    void testCreateInvalidTournament4() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            newTournament.setId(123L);
            newTournament.setName(tournamentList.get(0).getName());
            tournamentService.createTournament(newTournament);
        });
    }

    @Test
    void testCreateInvalidTournament5() {
        assertThrows(IllegalOperationException.class, () -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            newTournament.setId(123L);
            newTournament.setName("Test Name");
            newTournament.setDate(calendar.getTime());
            tournamentService.createTournament(newTournament);
        });
    }

    @Test
    void testGetTournaments() {
        List<TournamentEntity> tournaments = tournamentService.getTournaments();
        assertEquals(tournaments.size(), tournamentList.size());
        for (TournamentEntity tournament : tournaments) {
            boolean found = false;
            for (TournamentEntity storedTournament : tournamentList) {
                if (tournament.getId().equals(storedTournament.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void testGetTournament() throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity storedTournament = tournamentList.get(0);
        TournamentEntity tournament = tournamentService.getTournament(storedTournament.getId());
        assertNotNull(tournament);
        assertEquals(storedTournament.getId(), tournament.getId());
        assertEquals(storedTournament.getName(), tournament.getName());
        assertEquals(storedTournament.getLocation(), tournament.getLocation());
        assertEquals(storedTournament.getDate(), tournament.getDate());
        assertEquals(storedTournament.getPrize(), tournament.getPrize());
    }

    @Test
    void testGetInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentService.getTournament(null);
        });
    }

    @Test
    void testGetInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentService.getTournament(0L);
        });
    }

    @Test
    void testGetInvalidTournament3() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentService.getTournament(321L);
        });
    }

    @Test
    void testUpdateTournament() throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity storedTournament = tournamentList.get(0);
        TournamentEntity tournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
        tournament.setId(storedTournament.getId());
        tournament.setName("Test Name");
        tournament.setLocation("Test Location");
        tournament.setDate(new Date());
        tournament.setPrize("Test Prize");
        TournamentEntity updatedTournament = tournamentService.updateTournament(storedTournament.getId(), tournament);
        assertEquals(tournament.getId(), updatedTournament.getId());
        assertEquals(tournament.getName(), updatedTournament.getName());
        assertEquals(tournament.getLocation(), updatedTournament.getLocation());
        assertEquals(tournament.getDate(), updatedTournament.getDate());
        assertEquals(tournament.getPrize(), updatedTournament.getPrize());
    }

    @Test
    void testUpdateInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity storedTournament = tournamentList.get(0);
            tournamentService.updateTournament(null, storedTournament);
        });
    }

    @Test
    void testUpdateInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity storedTournament = tournamentList.get(0);
            tournamentService.updateTournament(0L, storedTournament);
        });
    }

    @Test
    void testUpdateInvalidTournament3() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentService.updateTournament(321L, tournamentList.get(0));
        });
    }

    @Test
    void testUpdateInvalidTournament4() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity storedTournament = tournamentList.get(0);
            storedTournament.setName(null);
            tournamentService.updateTournament(storedTournament.getId(), storedTournament);
        });
    }
    @Test
    void testUpdateInvalidTournament5() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity storedTournament = tournamentList.get(0);
            storedTournament.setName(tournamentList.get(1).getName());
            tournamentService.updateTournament(storedTournament.getId(), storedTournament);
        });
    }

    @Test
    void testUpdateInvalidTournament6() {
        assertThrows(IllegalOperationException.class, () -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            TournamentEntity storedTournament = tournamentList.get(0);
            storedTournament.setName("Test Name");
            storedTournament.setDate(calendar.getTime());
            tournamentService.updateTournament(storedTournament.getId(), storedTournament);
        });
    }

    @Test
    void testDeleteTournament() throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity storedTournament = tournamentList.get(0);
        tournamentService.deleteTournament(storedTournament.getId());
        TournamentEntity deletedTournament = entityManager.find(TournamentEntity.class, storedTournament.getId());
        assertNull(deletedTournament);
    }

    @Test
    void testDeleteInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentService.deleteTournament(null);
        });
    }

    @Test
    void testDeleteInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentService.deleteTournament(0L);
        });
    }

    @Test
    void testDeleteInvalidTournament3() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentService.deleteTournament(321L);
        });
    }
}
