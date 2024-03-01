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
import co.edu.uniandes.dse.ligaajedrez.entities.OpeningEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({GameOpeningService.class, OpeningService.class, GameService.class})
public class GameOpeningServiceTest {
    @Autowired
    private GameOpeningService gameOpeningService;

    @Autowired
    private OpeningService openingService;

    @Autowired
    private GameService gameService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<OpeningEntity> openings = new ArrayList<>();
    private List<GameEntity> games = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from OpeningEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from GameEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            OpeningEntity opening = factory.manufacturePojoWithFullData(OpeningEntity.class);
            entityManager.persist(opening);
            openings.add(opening);
        }
        for (int i = 0; i < 3; i++) {
            GameEntity game = factory.manufacturePojoWithFullData(GameEntity.class);
            entityManager.persist(game);
            games.add(game);
        }
        games.get(0).setOpening(openings.get(0));
    }

    @Test
    void testAddOpening() throws IllegalOperationException, EntityNotFoundException {
        GameEntity game = games.get(1);
        OpeningEntity opening = openings.get(1);
        gameOpeningService.addOpening(game.getId(), opening.getId());
        OpeningEntity entity = openingService.getOpening(opening.getId());
        assertEquals(opening.getId(), entity.getId());
        assertEquals(opening.getName(), entity.getName());
        assertEquals(opening.getClassification(), entity.getClassification());
    }

    @Test
    void testAddInvalidOpening1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.addOpening(null, openings.get(1).getId());
        });
    }

    @Test
    void testAddInvalidOpening2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.addOpening(games.get(1).getId(), null);
        });
    }

    @Test
    void testAddInvalidOpening3() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.addOpening(0L, openings.get(1).getId());
        });
    }

    @Test
    void testAddInvalidOpening4() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.addOpening(games.get(1).getId(), 0L);
        });
    }

    @Test
    void testAddInvalidOpening5() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameOpeningService.addOpening(321L, openings.get(1).getId());
        });
    }

    @Test
    void testAddInvalidOpening6() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameOpeningService.addOpening(games.get(1).getId(), 321L);
        });
    }

    @Test
    void testGetOpening() throws IllegalOperationException, EntityNotFoundException {
        OpeningEntity opening = gameOpeningService.getOpening(games.get(0).getId());
        OpeningEntity entity = openingService.getOpening(openings.get(0).getId());
        assertEquals(opening.getId(), entity.getId());
        assertEquals(opening.getName(), entity.getName());
        assertEquals(opening.getClassification(), entity.getClassification());
    }

    @Test
    void testGetInvalidOpening1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.getOpening(null);
        });
    }

    @Test
    void testGetInvalidOpening2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.getOpening(0L);
        });
    }

    @Test
    void testGetInvalidOpening3() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameOpeningService.getOpening(321L);
        });
    }

    @Test
    void testReplaceOpening() throws IllegalOperationException, EntityNotFoundException {
        GameEntity game = games.get(0);
        OpeningEntity opening = openings.get(1);
        gameOpeningService.addOpening(game.getId(), opening.getId());
        OpeningEntity entity = openingService.getOpening(opening.getId());
        assertEquals(opening.getId(), entity.getId());
    }

    @Test
    void testReplaceInvalidOpening1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.replaceOpening(null, openings.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidOpening2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.replaceOpening(games.get(0).getId(), null);
        });
    }

    @Test
    void testReplaceInvalidOpening3() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.replaceOpening(0L, openings.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidOpening4() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.replaceOpening(games.get(0).getId(), 0L);
        });
    }

    @Test
    void testReplaceInvalidOpening5() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameOpeningService.replaceOpening(321L, openings.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidOpening6() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameOpeningService.replaceOpening(games.get(0).getId(), 321L);
        });
    }

    @Test
    void testRemoveOpening() throws IllegalOperationException, EntityNotFoundException {
        gameOpeningService.removeOpening(games.get(0).getId());
        GameEntity entity = gameService.getGame(games.get(0).getId());
        assertNull(entity.getOpening());
    }

    @Test
    void testRemoveInvalidOpening1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.removeOpening(null);
        });
    }

    @Test
    void testRemoveInvalidOpening2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameOpeningService.removeOpening(0L);
        });
    }

    @Test
    void testRemoveInvalidOpening3() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameOpeningService.removeOpening(321L);
        });
    }
}
