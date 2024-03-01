package co.edu.uniandes.dse.ligaajedrez.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({TournamentGameService.class, GameTournamentService.class})
public class TournamentGameServiceTest {
    @Autowired
    private TournamentGameService tournamentGameService;

    @Autowired
    private GameTournamentService gameTournamentService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private TournamentEntity tournament = new TournamentEntity();
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
        tournament = factory.manufacturePojoWithFullData(TournamentEntity.class);
        entityManager.persist(tournament);
        for (int i = 0; i < 3; i++) {
            GameEntity game = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(game);
            tournament.getGames().add(game);
            games.add(game);
        }
    }

    @Test
    void testAddGame() throws IllegalOperationException, EntityNotFoundException {
        GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
        entityManager.persist(newGame);
        tournamentGameService.addGame(tournament.getId(), newGame.getId());
        gameTournamentService.addTournament(newGame.getId(), tournament.getId());
        GameEntity entity = tournamentGameService.getGame(newGame.getId(), tournament.getId());
        assertEquals(newGame.getId(), entity.getId());
        assertEquals(newGame.getResult(), entity.getResult());
        assertEquals(newGame.getDate(), entity.getDate());
    }

    @Test
    void testAddInvalidGame1() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            tournamentGameService.addGame(null, newGame.getId());
        });
    }

    @Test
    void testAddInvalidGame2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentGameService.addGame(tournament.getId(), null);
        });
    }

    @Test
    void testAddInvalidGame3() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            tournamentGameService.addGame(0L, newGame.getId());
        });
    }

    @Test
    void testAddInvalidGame4() {
        assertThrows(IllegalOperationException.class, () -> {
            gameTournamentService.addTournament(tournament.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidGame5() {
        assertThrows(EntityNotFoundException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            tournamentGameService.addGame(321L, newGame.getId());
        });
    }

    @Test
    void testAddInvalidGame6() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameTournamentService.addTournament(tournament.getId(), 321L);
        });
    }

    @Test
    void testGetGames() throws IllegalOperationException, EntityNotFoundException {
        List<GameEntity> gameEntities = tournamentGameService.getGames(tournament.getId());
        assertEquals(gameEntities.size(), games.size());
        for (GameEntity game : gameEntities) {
            assertTrue(tournament.getGames().contains(game));
        }
    }

    @Test
    void testGetInvalidGames1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentGameService.getGames(null);
        });
    }

    @Test
    void testGetInvalidGames2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentGameService.getGames(0L);
        });
    }

    @Test
    void testGetInvalidGames3() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentGameService.getGames(321L);
        });
    }

    @Test
    void testGetGame() throws IllegalOperationException, EntityNotFoundException {
        GameEntity game = games.get(0);
        GameEntity entity = tournamentGameService.getGame(game.getId(), tournament.getId());
        assertNotNull(entity);
        assertEquals(game.getId(), entity.getId());
        assertEquals(game.getResult(), entity.getResult());
        assertEquals(game.getDate(), entity.getDate());
    }

    @Test
    void testGetInvalidGame1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentGameService.getGame(null, tournament.getId());
        });
    }

    @Test
    void testGetInvalidGame2() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity game = games.get(0);
            tournamentGameService.getGame(game.getId(), null);
        });
    }

    @Test
    void testGetInvalidGame3() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentGameService.getGame(0L, tournament.getId());
        });
    }

    @Test
    void testGetInvalidGame4() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity game = games.get(0);
            tournamentGameService.getGame(game.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidGame5() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentGameService.getGame(321L, tournament.getId());
        });
    }

    @Test
    void testGetInvalidGame6() {
        assertThrows(EntityNotFoundException.class, () -> {
            GameEntity game = games.get(0);
            tournamentGameService.getGame(game.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidGame7() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            tournamentGameService.getGame(newGame.getId(), tournament.getId());
        });
    }

    @Test
    void testReplaceGames() throws IllegalOperationException, EntityNotFoundException {
        List<GameEntity> newGames = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            GameEntity game = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(game);
            newGames.add(game);
        }
        tournamentGameService.replaceGames(tournament.getId(), newGames);
        List<GameEntity> gameEntities = tournamentGameService.getGames(tournament.getId());
        assertEquals(gameEntities.size(), newGames.size());
        for (GameEntity game : gameEntities) {
            assertTrue(newGames.contains(game));
        }
    }

    @Test
    void testReplaceInvalidGames1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<GameEntity> newGames = new ArrayList<>();
            tournamentGameService.replaceGames(null, newGames);
        });
    }

    @Test
    void testReplaceInvalidGames2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<GameEntity> newGames = new ArrayList<>();
            tournamentGameService.replaceGames(0L, newGames);
        });
    }

    @Test
    void testReplaceInvalidGames3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<GameEntity> newGames = new ArrayList<>();
            tournamentGameService.replaceGames(321L, newGames);
        });
    }

    @Test
    void testReplaceInvalidGames4() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<GameEntity> newGames = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                GameEntity game = factory.manufacturePojoWithFullData(GameEntity.class);
                entityManager.persist(game);
                newGames.add(game);
            }
            GameEntity invalidGame = factory.manufacturePojoWithFullData(GameEntity.class);
            invalidGame.setId(321L);
            newGames.add(invalidGame);
            tournamentGameService.replaceGames(tournament.getId(), newGames);
        });
    }

    @Test
    void testRemoveGame() throws IllegalOperationException, EntityNotFoundException {
        for (GameEntity game : games) {
            tournamentGameService.removeGame(tournament.getId(), game.getId());
        }
        assertTrue(tournamentGameService.getGames(tournament.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidGame1() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentGameService.removeGame(null, games.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidGame2() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentGameService.removeGame(tournament.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidGame3() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            tournamentGameService.removeGame(0L, newGame.getId());
        });
    }

    @Test
    void testRemoveInvalidGame4() {
        assertThrows(IllegalOperationException.class, () -> {
            tournamentGameService.removeGame(tournament.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidGame5() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentGameService.removeGame(321L, games.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidGame6() {
        assertThrows(EntityNotFoundException.class, () -> {
            tournamentGameService.removeGame(tournament.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidGame7() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            tournamentGameService.removeGame(tournament.getId(), newGame.getId());
        });
    }
}
