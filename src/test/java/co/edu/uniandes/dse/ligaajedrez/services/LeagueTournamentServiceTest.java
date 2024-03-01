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
import co.edu.uniandes.dse.ligaajedrez.entities.TournamentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({LeagueTournamentService.class, TournamentLeagueService.class})
public class LeagueTournamentServiceTest {
    @Autowired
    private LeagueTournamentService leagueTournamentService;

    @Autowired
    private TournamentLeagueService tournamentLeagueService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private LeagueEntity league = new LeagueEntity();
    private List<TournamentEntity> tournaments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from LeagueEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from TournamentEntity").executeUpdate();
    }

    private void insertData() {
        league = factory.manufacturePojoWithFullData(LeagueEntity.class);
        entityManager.persist(league);
        for (int i = 0; i < 3; i++) {
            TournamentEntity tournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(tournament);
            league.getTournaments().add(tournament);
            tournaments.add(tournament);
        }
    }

    @Test
    void testAddTournament() throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
        entityManager.persist(newTournament);
        leagueTournamentService.addTournament(league.getId(), newTournament.getId());
        tournamentLeagueService.addLeague(newTournament.getId(), league.getId());
        TournamentEntity entity = leagueTournamentService.getTournament(newTournament.getId(), league.getId());
        assertEquals(newTournament.getId(), entity.getId());
        assertEquals(newTournament.getName(), entity.getName());
        assertEquals(newTournament.getLocation(), entity.getLocation());
        assertEquals(newTournament.getDate(), entity.getDate());
        assertEquals(newTournament.getPrize(), entity.getPrize());
        assertEquals(newTournament.getImage(), entity.getImage());
    }

    @Test
    void testAddInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            leagueTournamentService.addTournament(null, newTournament.getId());
        });
    }

    @Test
    void testAddInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueTournamentService.addTournament(league.getId(), null);
        });
    }

    @Test
    void testAddInvalidTournament3() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            leagueTournamentService.addTournament(0L, newTournament.getId());
        });
    }

    @Test
    void testAddInvalidTournament4() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.addLeague(league.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidTournament5() {
        assertThrows(EntityNotFoundException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            leagueTournamentService.addTournament(321L, newTournament.getId());
        });
    }

    @Test
    void testAddInvalidTournament6() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentLeagueService.addLeague(league.getId(), 321L);
        });
    }

    @Test
    void testGetTournaments() throws IllegalOperationException, EntityNotFoundException {
        List<TournamentEntity> tournamentEntities = leagueTournamentService.getTournaments(league.getId());
        assertEquals(tournamentEntities.size(), tournaments.size());
        for (TournamentEntity tournament : tournamentEntities) {
            assertTrue(league.getTournaments().contains(tournament));
        }
    }

    @Test
    void testGetInvalidTournaments1() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueTournamentService.getTournaments(null);
        });
    }

    @Test
    void testGetInvalidTournaments2() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueTournamentService.getTournaments(0L);
        });
    }

    @Test
    void testGetInvalidTournaments3() {
        assertThrows(EntityNotFoundException.class, () -> {
            leagueTournamentService.getTournaments(321L);
        });
    }

    @Test
    void testGetTournament() throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity tournament = tournaments.get(0);
        TournamentEntity entity = leagueTournamentService.getTournament(tournament.getId(), league.getId());
        assertNotNull(entity);
        assertEquals(tournament.getId(), entity.getId());
        assertEquals(tournament.getName(), entity.getName());
        assertEquals(tournament.getLocation(), entity.getLocation());
        assertEquals(tournament.getDate(), entity.getDate());
        assertEquals(tournament.getPrize(), entity.getPrize());
        assertEquals(tournament.getImage(), entity.getImage());
    }

    @Test
    void testGetInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueTournamentService.getTournament(null, league.getId());
        });
    }

    @Test
    void testGetInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity tournament = tournaments.get(0);
            leagueTournamentService.getTournament(tournament.getId(), null);
        });
    }

    @Test
    void testGetInvalidTournament3() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueTournamentService.getTournament(0L, league.getId());
        });
    }

    @Test
    void testGetInvalidTournament4() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity tournament = tournaments.get(0);
            leagueTournamentService.getTournament(tournament.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidTournament5() {
        assertThrows(EntityNotFoundException.class, () -> {
            leagueTournamentService.getTournament(321L, league.getId());
        });
    }

    @Test
    void testGetInvalidTournament6() {
        assertThrows(EntityNotFoundException.class, () -> {
            TournamentEntity tournament = tournaments.get(0);
            leagueTournamentService.getTournament(tournament.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidTournament7() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            leagueTournamentService.getTournament(newTournament.getId(), league.getId());
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
        leagueTournamentService.replaceTournaments(league.getId(), newTournaments);
        List<TournamentEntity> tournamentEntities = leagueTournamentService.getTournaments(league.getId());
        assertEquals(tournamentEntities.size(), newTournaments.size());
        for (TournamentEntity tournament : tournamentEntities) {
            assertTrue(newTournaments.contains(tournament));
        }
    }

    @Test
    void testReplaceInvalidTournaments1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<TournamentEntity> newTournaments = new ArrayList<>();
            leagueTournamentService.replaceTournaments(null, newTournaments);
        });
    }

    @Test
    void testReplaceInvalidTournaments2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<TournamentEntity> newTournaments = new ArrayList<>();
            leagueTournamentService.replaceTournaments(0L, newTournaments);
        });
    }

    @Test
    void testReplaceInvalidTournaments3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<TournamentEntity> newTournaments = new ArrayList<>();
            leagueTournamentService.replaceTournaments(321L, newTournaments);
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
            leagueTournamentService.replaceTournaments(league.getId(), newTournaments);
        });
    }

    @Test
    void testRemoveTournament() throws IllegalOperationException, EntityNotFoundException {
        for (TournamentEntity tournament : tournaments) {
            leagueTournamentService.removeTournament(league.getId(), tournament.getId());
        }
        assertTrue(leagueTournamentService.getTournaments(league.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueTournamentService.removeTournament(null, tournaments.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueTournamentService.removeTournament(league.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidTournament3() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            leagueTournamentService.removeTournament(0L, newTournament.getId());
        });
    }

    @Test
    void testRemoveInvalidTournament4() {
        assertThrows(IllegalOperationException.class, () -> {
            leagueTournamentService.removeTournament(league.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidTournament5() {
        assertThrows(EntityNotFoundException.class, () -> {
            leagueTournamentService.removeTournament(321L, tournaments.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidTournament6() {
        assertThrows(EntityNotFoundException.class, () -> {
            leagueTournamentService.removeTournament(league.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidTournament7() {
        assertThrows(IllegalOperationException.class, () -> {
            TournamentEntity newTournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(newTournament);
            leagueTournamentService.removeTournament(league.getId(), newTournament.getId());
        });
    }
}
