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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(GameService.class)
public class GameServiceTest {
    @Autowired
    private GameService gameService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<GameEntity> gameList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from GameEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            GameEntity gameEntity = factory.manufacturePojo(GameEntity.class);
            gameEntity.setDate(new Date());
            entityManager.persist(gameEntity);
            gameList.add(gameEntity);
        }
    }

    @Test
    void testCreateGame() throws IllegalOperationException {
        GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
        newGame.setId(123L);
        newGame.setResult("Test Result");
        newGame.setDate(new Date());
        GameEntity gameEntity = gameService.createGame(newGame);
        assertNotNull(gameEntity);
        GameEntity entity = entityManager.find(GameEntity.class, gameEntity.getId());
        assertEquals(gameEntity.getId(), entity.getId());
        assertEquals(gameEntity.getResult(), entity.getResult());
        assertEquals(gameEntity.getDate(), entity.getDate());
    }

    @Test
    void testCreateInvalidGame1() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            newGame.setId(null);
            newGame.setResult("Test Result");
            newGame.setDate(new Date());
            gameService.createGame(newGame);
        });
    }

    @Test
    void testCreateInvalidGame2() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            newGame.setId(0L);
            newGame.setResult("Test Result");
            newGame.setDate(new Date());
            gameService.createGame(newGame);
        });
    }

    @Test
    void testCreateInvalidGame3() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            newGame.setId(123L);
            newGame.setResult(null);
            newGame.setDate(new Date());
            gameService.createGame(newGame);
        });
    }

    @Test
    void testCreateInvalidGame4() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            newGame.setId(123L);
            newGame.setResult("Test Result");
            newGame.setDate(null);
            gameService.createGame(newGame);
        });
    }

    @Test
    void testCreateInvalidGame5() {
        assertThrows(IllegalOperationException.class, () -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, 1);
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            newGame.setId(123L);
            newGame.setResult("Test Result");
            newGame.setDate(new Date());
            newGame.setDate(calendar.getTime());
            gameService.createGame(newGame);
        });
    }

    @Test
    void testGetGames() {
        Pageable pageable = PageRequest.of(0, gameList.size());
        List<GameEntity> games = gameService.getGames(pageable);
        assertEquals(games.size(), gameList.size());
        for (GameEntity game : games) {
            boolean found = false;
            for (GameEntity storedGame : gameList) {
                if (game.getId().equals(storedGame.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void testGetGame() throws IllegalOperationException, EntityNotFoundException {
        GameEntity storedGame = gameList.get(0);
        GameEntity game = gameService.getGame(storedGame.getId());
        assertNotNull(game);
        assertEquals(storedGame.getId(), game.getId());
        assertEquals(storedGame.getResult(), game.getResult());
        assertEquals(storedGame.getDate(), game.getDate());
    }

    @Test
    void testGetInvalidGame1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameService.getGame(null);
        });
    }

    @Test
    void testGetInvalidGame2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameService.getGame(0L);
        });
    }

    @Test
    void testGetInvalidGame3() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameService.getGame(321L);
        });
    }

    @Test
    void testUpdateGame() throws IllegalOperationException, EntityNotFoundException {
        GameEntity storedGame = gameList.get(0);
        GameEntity game = factory.manufacturePojoWithFullData(GameEntity.class);
        game.setId(storedGame.getId());
        game.setResult("Updated Result");
        game.setDate(new Date());
        gameService.updateGame(storedGame.getId(), game);
        GameEntity updatedGame = entityManager.find(GameEntity.class, storedGame.getId());
        assertEquals(game.getId(), updatedGame.getId());
        assertEquals(game.getResult(), updatedGame.getResult());
        assertEquals(game.getDate(), updatedGame.getDate());
        assertEquals(game.getTournament(), updatedGame.getTournament());
    }

    @Test
    void testUpdateInvalidGame1() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity storedGame = gameList.get(0);
            gameService.updateGame(null, storedGame);
        });
    }

    @Test
    void testUpdateInvalidGame2() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity storedGame = gameList.get(0);
            gameService.updateGame(0L, storedGame);
        });
    }

    @Test
    void testUpdateInvalidGame3() {
        assertThrows(EntityNotFoundException.class, () -> {
            GameEntity storedGame = gameList.get(0);
            gameService.updateGame(321L, storedGame);
        });
    }

    @Test
    void testUpdateInvalidGame4() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity storedGame = gameList.get(0);
            storedGame.setResult(null);
            storedGame.setDate(new Date());
            gameService.updateGame(storedGame.getId(), storedGame);
        });
    }

    @Test
    void testUpdateInvalidGame5() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity storedGame = gameList.get(0);
            storedGame.setResult("Updated Result");
            storedGame.setDate(null);
            gameService.updateGame(storedGame.getId(), storedGame);
        });
    }

    @Test
    void testUpdateInvalidGame6() {
        assertThrows(IllegalOperationException.class, () -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, 1);
            GameEntity storedGame = gameList.get(0);
            storedGame.setResult("Updated Result");
            storedGame.setDate(calendar.getTime());
            gameService.updateGame(storedGame.getId(), storedGame);
        });
    }

    @Test
    void testDeleteGame() throws IllegalOperationException, EntityNotFoundException {
        GameEntity storedGame = gameList.get(0);
        gameService.deleteGame(storedGame.getId());
        GameEntity deletedGame = entityManager.find(GameEntity.class, storedGame.getId());
        assertNull(deletedGame);
    }

    @Test
    void testDeleteInvalidGame1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameService.deleteGame(null);
        });
    }

    @Test
    void testDeleteInvalidGame2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameService.deleteGame(0L);
        });
    }

    @Test
    void testDeleteInvalidGame3() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameService.deleteGame(321L);
        });
    }
}
