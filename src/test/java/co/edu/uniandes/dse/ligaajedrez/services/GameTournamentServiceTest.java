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

import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.TournamentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({GameTournamentService.class, TournamentService.class, GameService.class})
public class GameTournamentServiceTest {
    @Autowired
    private GameTournamentService gameTournamentService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private GameService gameService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<TournamentEntity> tournaments = new ArrayList<>();
    private List<GameEntity> games = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from TournamentEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from GameEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            TournamentEntity tournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
            entityManager.persist(tournament);
            tournaments.add(tournament);
        }
        for (int i = 0; i < 3; i++) {
            GameEntity game = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(game);
            games.add(game);
        }
        games.get(0).setTournament(tournaments.get(0));
    }

    @Test
    void testAddTournament() throws IllegalOperationException, EntityNotFoundException {
        GameEntity game = games.get(1);
        TournamentEntity tournament = tournaments.get(1);
        gameTournamentService.addTournament(game.getId(), tournament.getId());
        TournamentEntity entity = tournamentService.getTournament(tournament.getId());
        assertEquals(tournament.getId(), entity.getId());
        assertEquals(tournament.getName(), entity.getName());
        assertEquals(tournament.getLocation(), entity.getLocation());
        assertEquals(tournament.getDate(), entity.getDate());
        assertEquals(tournament.getPrize(), entity.getPrize());
        assertEquals(tournament.getImage(), entity.getImage());
    }

    @Test
    void testAddInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.addTournament(null, tournaments.get(1).getId());
        });
    }

    @Test
    void testAddInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.addTournament(games.get(1).getId(), null);
        });
    }

    @Test
    void testAddInvalidTournament3() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.addTournament(0L, tournaments.get(1).getId());
        });
    }

    @Test
    void testAddInvalidTournament4() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.addTournament(games.get(1).getId(), 0L);
        });
    }

    @Test
    void testAddInvalidTournament5() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameTournamentService.addTournament(321L, tournaments.get(1).getId());
        });
    }

    @Test
    void testAddInvalidTournament6() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameTournamentService.addTournament(games.get(1).getId(), 321L);
        });
    }

    @Test
    void testGetTournament() throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity tournament = gameTournamentService.getTournament(games.get(0).getId());
        TournamentEntity entity = tournamentService.getTournament(tournaments.get(0).getId());
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
            gameTournamentService.getTournament(null);
        });
    }

    @Test
    void testGetInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.getTournament(0L);
        });
    }

    @Test
    void testGetInvalidTournament3() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameTournamentService.getTournament(321L);
        });
    }

    @Test
    void testReplaceTournament() throws IllegalOperationException, EntityNotFoundException {
        GameEntity game = games.get(0);
        TournamentEntity tournament = tournaments.get(1);
        gameTournamentService.addTournament(game.getId(), tournament.getId());
        TournamentEntity entity = tournamentService.getTournament(tournament.getId());
        assertEquals(tournament.getId(), entity.getId());
    }

    @Test
    void testReplaceInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.replaceTournament(null, tournaments.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.replaceTournament(games.get(0).getId(), null);
        });
    }

    @Test
    void testReplaceInvalidTournament3() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.replaceTournament(0L, tournaments.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidTournament4() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.replaceTournament(games.get(0).getId(), 0L);
        });
    }

    @Test
    void testReplaceInvalidTournament5() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameTournamentService.replaceTournament(321L, tournaments.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidTournament6() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameTournamentService.replaceTournament(games.get(0).getId(), 321L);
        });
    }

    @Test
    void testRemoveTournament() throws IllegalOperationException, EntityNotFoundException {
        gameTournamentService.removeTournament(games.get(0).getId());
        GameEntity entity = gameService.getGame(games.get(0).getId());
        assertNull(entity.getTournament());
    }

    @Test
    void testRemoveInvalidTournament1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.removeTournament(null);
        });
    }

    @Test
    void testRemoveInvalidTournament2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.removeTournament(0L);
        });
    }

    @Test
    void testRemoveInvalidTournament3() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameTournamentService.removeTournament(321L);
        });
    }
}
