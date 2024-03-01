package co.edu.uniandes.dse.ligaajedrez.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
@Import({TournamentLeagueService.class, LeagueService.class, TournamentService.class})
public class TournamentLeagueServiceTest {
    @Autowired
    private TournamentLeagueService tournamentLeagueService;

    @Autowired
    private LeagueService leagueService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<TournamentEntity> tournaments = new ArrayList<>();
    private List<LeagueEntity> leagues = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from TournamentEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from LeagueEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            TournamentEntity tournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(tournament);
            tournaments.add(tournament);
        }
        for (int i = 0; i < 3; i++) {
            LeagueEntity league = factory.manufacturePojoWithFullData(LeagueEntity.class);
            entityManager.persist(league);
            leagues.add(league);
        }
        leagues.get(0).getTournaments().add(tournaments.get(0));
        tournaments.get(0).setLeague(leagues.get(0));
    }

    @Test
    void testAddLeague() throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity tournament = tournaments.get(1);
        LeagueEntity league = leagues.get(1);
        tournamentLeagueService.addLeague(tournament.getId(), league.getId());
        LeagueEntity entity = leagueService.getLeague(league.getId());
        assertEquals(league.getId(), entity.getId());
        assertEquals(league.getName(), entity.getName());
        assertEquals(league.getCity(), entity.getCity());
        assertEquals(league.getAddress(), entity.getAddress());
        assertEquals(league.getPhone(), entity.getPhone());
        assertEquals(league.getWebURL(), entity.getWebURL());
    }

    @Test
    void testAddInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.addLeague(null, leagues.get(1).getId());
        });
    }

    @Test
    void testAddInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.addLeague(tournaments.get(1).getId(), null);
        });
    }

    @Test
    void testAddInvalidLeague3() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.addLeague(0l, leagues.get(1).getId());
        });
    }

    @Test
    void testAddInvalidLeague4() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.addLeague(tournaments.get(1).getId(), 0L);
        });
    }

    @Test
    void testAddInvalidLeague5() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentLeagueService.addLeague(321L, leagues.get(1).getId());
        });
    }

    @Test
    void testAddInvalidLeague6() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentLeagueService.addLeague(tournaments.get(1).getId(), 321L);
        });
    }

    @Test
    void testGetLeague() throws IllegalOperationException, EntityNotFoundException {
        LeagueEntity league = tournamentLeagueService.getLeague(tournaments.get(0).getId());
        LeagueEntity entity = leagueService.getLeague(leagues.get(0).getId());
        assertEquals(league.getId(), entity.getId());
        assertEquals(league.getName(), entity.getName());
        assertEquals(league.getCity(), entity.getCity());
        assertEquals(league.getAddress(), entity.getAddress());
        assertEquals(league.getPhone(), entity.getPhone());
        assertEquals(league.getWebURL(), entity.getWebURL());
    }

    @Test
    void testGetInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.getLeague(null);
        });
    }

    @Test
    void testGetInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.getLeague(0L);
        });
    }

    @Test
    void testGetInvalidLeague3() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentLeagueService.getLeague(321L);
        });
    }

    @Test
    void testReplaceLeague() throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity tournament = tournaments.get(0);
        LeagueEntity league = leagues.get(1);
        tournamentLeagueService.addLeague(tournament.getId(), league.getId());
        LeagueEntity entity = leagueService.getLeague(league.getId());
        assertEquals(league.getId(), entity.getId());
        assertEquals(league.getName(), entity.getName());
        assertEquals(league.getCity(), entity.getCity());
        assertEquals(league.getAddress(), entity.getAddress());
        assertEquals(league.getPhone(), entity.getPhone());
        assertEquals(league.getWebURL(), entity.getWebURL());
    }

    @Test
    void testReplaceInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.replaceLeague(null, leagues.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.replaceLeague(tournaments.get(0).getId(), null);
        });
    }

    @Test
    void testReplaceInvalidLeague3() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.replaceLeague(0L, leagues.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidLeague4() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.replaceLeague(tournaments.get(0).getId(), 0L);
        });
    }

    @Test
    void testReplaceInvalidLeague5() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentLeagueService.replaceLeague(321L, leagues.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidLeague6() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentLeagueService.replaceLeague(tournaments.get(0).getId(), 321L);
        });
    }

    @Test
    void testRemoveLeague() throws IllegalOperationException, EntityNotFoundException {
        tournamentLeagueService.removeLeague(tournaments.get(0).getId());
        TournamentEntity entity = tournamentService.getTournament(tournaments.get(0).getId());
        assertNull(entity.getLeague());
    }

    @Test
    void testRemoveInvalidLeague1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.removeLeague(null);
        });
    }

    @Test
    void testRemoveInvalidLeague2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentLeagueService.removeLeague(0L);
        });
    }

    @Test
    void testRemoveInvalidLeague3() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentLeagueService.removeLeague(321L);
        });
    }
}
