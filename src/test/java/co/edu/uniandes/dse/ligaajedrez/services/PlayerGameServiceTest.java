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

import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.PlayerEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({PlayerGameService.class, GamePlayerService.class})
public class PlayerGameServiceTest {
    @Autowired
    private PlayerGameService playerGameService;

    @Autowired
    private GamePlayerService gamePlayerService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private PlayerEntity player = new PlayerEntity();
    private List<GameEntity> games = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PlayerEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from GameEntity").executeUpdate();
    }

    private void insertData() {
        player = factory.manufacturePojoWithFullData(PlayerEntity.class);
        entityManager.persist(player);
        for (int i = 0; i < 3; i++) {
            GameEntity game = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(game);
            player.getGames().add(game);
            game.getPlayers().add(player);
            games.add(game);
        }
    }

    @Test
    void testAddGame() throws IllegalOperationException, EntityNotFoundException {
        GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
        entityManager.persist(newGame);
        playerGameService.addGame(player.getId(), newGame.getId());
        gamePlayerService.addPlayer(newGame.getId(), player.getId());
        GameEntity entity = playerGameService.getGame(newGame.getId(), player.getId());
        assertEquals(newGame.getId(), entity.getId());
        assertEquals(newGame.getResult(), entity.getResult());
        assertEquals(newGame.getDate(), entity.getDate());
    }

    @Test
    void testAddInvalidGame1() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            playerGameService.addGame(null, newGame.getId());
        });
    }

    @Test
    void testAddInvalidGame2() {
        assertThrows(IllegalOperationException.class, () -> {
            playerGameService.addGame(player.getId(), null);
        });
    }

    @Test
    void testAddInvalidGame3() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            playerGameService.addGame(0L, newGame.getId());
        });
    }

    @Test
    void testAddInvalidGame4() {
        assertThrows(IllegalOperationException.class, () -> {
            playerGameService.addGame(player.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidGame5() {
        assertThrows(EntityNotFoundException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            playerGameService.addGame(321L, newGame.getId());
        });
    }

    @Test
    void testAddInvalidGame6() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerGameService.addGame(player.getId(), 321L);
        });
    }

    @Test
    void testGetGames() throws IllegalOperationException, EntityNotFoundException {
        List<GameEntity> gameEntities = playerGameService.getGames(player.getId());
        assertEquals(gameEntities.size(), games.size());
        for (GameEntity game : gameEntities) {
            assertTrue(player.getGames().contains(game));
        }
    }

    @Test
    void testGetInvalidGames1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerGameService.getGames(null);
        });
    }

    @Test
    void testGetInvalidGames2() {
        assertThrows(IllegalOperationException.class, () -> {
            playerGameService.getGames(0L);
        });
    }

    @Test
    void testGetInvalidGames3() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerGameService.getGames(321L);
        });
    }

    @Test
    void testGetGame() throws IllegalOperationException, EntityNotFoundException {
        GameEntity game = games.get(0);
        GameEntity entity = playerGameService.getGame(game.getId(), player.getId());
        assertNotNull(entity);
        assertEquals(game.getId(), entity.getId());
        assertEquals(game.getResult(), entity.getResult());
        assertEquals(game.getDate(), entity.getDate());
    }

    @Test
    void testGetInvalidGame1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerGameService.getGame(null, player.getId());
        });
    }

    @Test
    void testGetInvalidGame2() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity game = games.get(0);
            playerGameService.getGame(game.getId(), null);
        });
    }

    @Test
    void testGetInvalidGame3() {
        assertThrows(IllegalOperationException.class, () -> {
            playerGameService.getGame(0L, player.getId());
        });
    }

    @Test
    void testGetInvalidGame4() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity game = games.get(0);
            playerGameService.getGame(game.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidGame5() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerGameService.getGame(321L, player.getId());
        });
    }

    @Test
    void testGetInvalidGame6() {
        assertThrows(EntityNotFoundException.class, () -> {
            GameEntity game = games.get(0);
            playerGameService.getGame(game.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidGame7() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            playerGameService.getGame(newGame.getId(), player.getId());
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
        playerGameService.replaceGames(player.getId(), newGames);
        List<GameEntity> gameEntities = playerGameService.getGames(player.getId());
        assertEquals(gameEntities.size(), newGames.size());
        for (GameEntity game : gameEntities) {
            assertTrue(newGames.contains(game));
        }
    }

    @Test
    void testReplaceInvalidGames1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<GameEntity> newGames = new ArrayList<>();
            playerGameService.replaceGames(null, newGames);
        });
    }

    @Test
    void testReplaceInvalidGames2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<GameEntity> newGames = new ArrayList<>();
            playerGameService.replaceGames(0L, newGames);
        });
    }

    @Test
    void testReplaceInvalidGames3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<GameEntity> newGames = new ArrayList<>();
            playerGameService.replaceGames(321L, newGames);
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
            playerGameService.replaceGames(player.getId(), newGames);
        });
    }

    @Test
    void testRemoveGame() throws IllegalOperationException, EntityNotFoundException {
        for (GameEntity game : games) {
            playerGameService.removeGame(player.getId(), game.getId());
        }
        assertTrue(playerGameService.getGames(player.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidGame1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerGameService.removeGame(null, games.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidGame2() {
        assertThrows(IllegalOperationException.class, () -> {
            playerGameService.removeGame(player.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidGame3() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            playerGameService.removeGame(0L, newGame.getId());
        });
    }

    @Test
    void testRemoveInvalidGame4() {
        assertThrows(IllegalOperationException.class, () -> {
            playerGameService.removeGame(player.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidGame5() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerGameService.removeGame(321L, games.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidGame6() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerGameService.removeGame(player.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidGame7() {
        assertThrows(IllegalOperationException.class, () -> {
            GameEntity newGame = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(newGame);
            playerGameService.removeGame(player.getId(), newGame.getId());
        });
    }
}
