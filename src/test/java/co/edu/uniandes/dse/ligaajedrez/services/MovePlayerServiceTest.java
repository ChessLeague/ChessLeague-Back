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

import co.edu.uniandes.dse.ligaajedrez.entities.MoveEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.PlayerEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({MovePlayerService.class, PlayerService.class, MoveService.class})
public class MovePlayerServiceTest {
    @Autowired
    private MovePlayerService movePlayerService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private MoveService moveService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<PlayerEntity> players = new ArrayList<>();
    private List<MoveEntity> moves = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PlayerEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from MoveEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            PlayerEntity player = factory.manufacturePojoWithFullData(PlayerEntity.class);
            entityManager.persist(player);
            players.add(player);
        }
        for (int i = 0; i < 3; i++) {
            MoveEntity move = factory.manufacturePojoWithFullData(MoveEntity.class);
            entityManager.persist(move);
            moves.add(move);
        }
        moves.get(0).setPlayer(players.get(0));
    }

    @Test
    void testAddPlayer() throws IllegalOperationException, EntityNotFoundException {
        MoveEntity move = moves.get(1);
        PlayerEntity player = players.get(1);
        movePlayerService.addPlayer(move.getId(), player.getId());
        PlayerEntity entity = playerService.getPlayer(player.getId());
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
    void testAddInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.addPlayer(null, players.get(1).getId());
        });
    }

    @Test
    void testAddInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.addPlayer(moves.get(1).getId(), null);
        });
    }

    @Test
    void testAddInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.addPlayer(0L, players.get(1).getId());
        });
    }

    @Test
    void testAddInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.addPlayer(moves.get(1).getId(), 0L);
        });
    }

    @Test
    void testAddInvalidPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            movePlayerService.addPlayer(321L, players.get(1).getId());
        });
    }

    @Test
    void testAddInvalidPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            movePlayerService.addPlayer(moves.get(1).getId(), 321L);
        });
    }

    @Test
    void testGetPlayer() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity player = movePlayerService.getPlayer(moves.get(0).getId());
        PlayerEntity entity = playerService.getPlayer(players.get(0).getId());
        assertEquals(player.getId(), entity.getId());
    }

    @Test
    void testGetInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.getPlayer(null);
        });
    }

    @Test
    void testGetInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.getPlayer(0L);
        });
    }

    @Test
    void testGetInvalidPlayer3() {
        assertThrows(EntityNotFoundException.class, () -> {
            movePlayerService.getPlayer(321L);
        });
    }

    @Test
    void testReplacePlayer() throws IllegalOperationException, EntityNotFoundException {
        MoveEntity move = moves.get(0);
        PlayerEntity player = players.get(1);
        movePlayerService.addPlayer(move.getId(), player.getId());
        PlayerEntity entity = playerService.getPlayer(player.getId());
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
    void testReplaceInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.replacePlayer(null, players.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.replacePlayer(moves.get(0).getId(), null);
        });
    }

    @Test
    void testReplaceInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.replacePlayer(0L, players.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.replacePlayer(moves.get(0).getId(), 0L);
        });
    }

    @Test
    void testReplaceInvalidPlayer5() {
        assertThrows(EntityNotFoundException.class, () -> {
            movePlayerService.replacePlayer(321L, players.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidPlayer6() {
        assertThrows(EntityNotFoundException.class, () -> {
            movePlayerService.replacePlayer(moves.get(0).getId(), 321L);
        });
    }

    @Test
    void testRemovePlayer() throws IllegalOperationException, EntityNotFoundException {
        movePlayerService.removePlayer(moves.get(0).getId());
        MoveEntity entity = moveService.getMove(moves.get(0).getId());
        assertNull(entity.getPlayer());
    }

    @Test
    void testRemoveInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.removePlayer(null);
        });
    }

    @Test
    void testRemoveInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            movePlayerService.removePlayer(0L);
        });
    }

    @Test
    void testRemoveInvalidPlayer3() {
        assertThrows(EntityNotFoundException.class, () -> {
            movePlayerService.removePlayer(321L);
        });
    }
}
