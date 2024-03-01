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
@Import({GamePlayerService.class, PlayerGameService.class})
public class GamePlayerServiceTest {
    @Autowired
    private GamePlayerService gamePlayerService;

    @Autowired
    private PlayerGameService playerGameService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private GameEntity game = new GameEntity();
    private List<PlayerEntity> players = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from GameEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PlayerEntity").executeUpdate();
    }

    private void insertData() {
        game = factory.manufacturePojoWithFullData(GameEntity.class);
        entityManager.persist(game);
        for (int i = 0; i < 3; i++) {
            PlayerEntity player = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(player);
            player.getGames().add(game);
            game.getPlayers().add(player);
            players.add(player);
        }
    }

    @Test
    void testAddPlayer() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
        entityManager.persist(newPlayer);
        gamePlayerService.addPlayer(game.getId(), newPlayer.getId());
        playerGameService.addGame(newPlayer.getId(), game.getId());
        PlayerEntity entity = gamePlayerService.getPlayer(newPlayer.getId(), game.getId());
        assertEquals(newPlayer.getId(), entity.getId());
        assertEquals(newPlayer.getName(), entity.getName());
        assertEquals(newPlayer.getPhotoURL(), entity.getPhotoURL());
        assertEquals(newPlayer.getBirthDate(), entity.getBirthDate());
        assertEquals(newPlayer.getBirthPlace(), entity.getBirthPlace());
        assertEquals(newPlayer.getEloRating(), entity.getEloRating());
        assertEquals(newPlayer.getUsername(), entity.getUsername());
        assertEquals(newPlayer.getPassword(), entity.getPassword());
    }

    @Test
    void testAddInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            gamePlayerService.addPlayer(null, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            gamePlayerService.addPlayer(game.getId(), null);
        });
    }

    @Test
    void testAddInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            gamePlayerService.addPlayer(0L, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            gamePlayerService.addPlayer(game.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            gamePlayerService.addPlayer(321L, newPlayer.getId());
        });
    }

    @Test
    void testAddInvalidPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            gamePlayerService.addPlayer(game.getId(), 321L);
        });
    }

    @Test
    void testGetPlayers() throws IllegalOperationException, EntityNotFoundException {
        List<PlayerEntity> playerEntities = gamePlayerService.getPlayers(game.getId());
        assertEquals(playerEntities.size(), players.size());
        for (PlayerEntity player : playerEntities) {
            assertTrue(game.getPlayers().contains(player));
        }
    }

    @Test
    void testGetInvalidPlayers1() {
        assertThrows(IllegalOperationException.class, () -> {
            gamePlayerService.getPlayers(null);
        });
    }

    @Test
    void testGetInvalidPlayers2() {
        assertThrows(IllegalOperationException.class, () -> {
            gamePlayerService.getPlayers(0L);
        });
    }

    @Test
    void testGetInvalidPlayers3() {
        assertThrows(EntityNotFoundException.class, () -> {
            gamePlayerService.getPlayers(321L);
        });
    }

    @Test
    void testGetPlayer() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity player = players.get(0);
        PlayerEntity entity = gamePlayerService.getPlayer(player.getId(), game.getId());
        assertNotNull(entity);
        assertEquals(player.getId(), entity.getId());
        assertEquals(player.getName(), entity.getName());
        assertEquals(player.getPhotoURL(), entity.getPhotoURL());
        assertEquals(player.getBirthDate(), entity.getBirthDate());
        assertEquals(player.getBirthPlace(), entity.getBirthPlace());
        assertEquals(player.getEloRating(), entity.getEloRating());
        assertEquals(player.getUsername(), entity.getUsername());
        assertEquals(player.getPassword(), entity.getPassword());
    }

    @Test
    void testGetInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            gamePlayerService.getPlayer(null, game.getId());
        });
    }

    @Test
    void testGetInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity player = players.get(0);
            gamePlayerService.getPlayer(player.getId(), null);
        });
    }

    @Test
    void testGetInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            gamePlayerService.getPlayer(0L, game.getId());
        });
    }

    @Test
    void testGetInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity player = players.get(0);
            gamePlayerService.getPlayer(player.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            gamePlayerService.getPlayer(321L, game.getId());
        });
    }

    @Test
    void testGetInvalidPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity player = players.get(0);
            gamePlayerService.getPlayer(player.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidPlayer7() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            gamePlayerService.getPlayer(newPlayer.getId(), game.getId());
        });
    }

    @Test
    void testReplacePlayers() throws IllegalOperationException, EntityNotFoundException {
        List<PlayerEntity> newPlayers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PlayerEntity player = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(player);
            newPlayers.add(player);
        }
        gamePlayerService.replacePlayers(game.getId(), newPlayers);
        List<PlayerEntity> playerEntities = gamePlayerService.getPlayers(game.getId());
        assertEquals(playerEntities.size(), newPlayers.size());
        for (PlayerEntity player : playerEntities) {
            assertTrue(newPlayers.contains(player));
        }
    }

    @Test
    void testReplaceInvalidPlayers1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            gamePlayerService.replacePlayers(null, newPlayers);
        });
    }

    @Test
    void testReplaceInvalidPlayers2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            gamePlayerService.replacePlayers(0L, newPlayers);
        });
    }

    @Test
    void testReplaceInvalidPlayers3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            gamePlayerService.replacePlayers(321L, newPlayers);
        });
    }

    @Test
    void testReplaceInvalidPlayers4() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<PlayerEntity> newPlayers = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                PlayerEntity player = factory.manufacturePojoWithFullData(PlayerEntity.class);
                entityManager.persist(player);
                newPlayers.add(player);
            }
            PlayerEntity invalidPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            invalidPlayer.setId(321L);
            newPlayers.add(invalidPlayer);
            gamePlayerService.replacePlayers(game.getId(), newPlayers);
        });
    }

    @Test
    void testRemovePlayer() throws IllegalOperationException, EntityNotFoundException {
        for (PlayerEntity player : players) {
            gamePlayerService.removePlayer(game.getId(), player.getId());
        }
        assertTrue(gamePlayerService.getPlayers(game.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            gamePlayerService.removePlayer(null, game.getId());
        });
    }

    @Test
    void testRemoveInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            gamePlayerService.removePlayer(0L, game.getId());
        });
    }

    @Test
    void testRemoveInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            gamePlayerService.removePlayer(newPlayer.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            gamePlayerService.removePlayer(newPlayer.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            gamePlayerService.removePlayer(321L, game.getId());
        });
    }

    @Test
    void testRemoveInvalidPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            gamePlayerService.removePlayer(newPlayer.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidPlayer7() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(newPlayer);
            gamePlayerService.removePlayer(game.getId(), newPlayer.getId());
        });
    }
}
