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

import co.edu.uniandes.dse.ligaajedrez.entities.OpeningEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.MoveEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(OpeningMoveService.class)
public class OpeningMoveServiceTest {
    @Autowired
    private OpeningMoveService openingMoveService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private OpeningEntity opening = new OpeningEntity();
    private List<MoveEntity> moves = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from OpeningEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from MoveEntity").executeUpdate();
    }

    private void insertData() {
        opening = factory.manufacturePojoWithFullData(OpeningEntity.class);
        entityManager.persist(opening);
        for (int i = 0; i < 3; i++) {
            MoveEntity move = factory.manufacturePojoWithFullData(MoveEntity.class);
            entityManager.persist(move);
            opening.getMoves().add(move);
            moves.add(move);
        }
    }

    @Test
    void testAddMove() throws IllegalOperationException, EntityNotFoundException {
        MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
        entityManager.persist(newMove);
        openingMoveService.addMove(opening.getId(), newMove.getId());
        MoveEntity entity = openingMoveService.getMove(newMove.getId(), opening.getId());
        assertEquals(newMove.getId(), entity.getId());
        assertEquals(newMove.getNotation(), entity.getNotation());
    }

    @Test
    void testAddInvalidMove1() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
            entityManager.persist(newMove);
            openingMoveService.addMove(null, newMove.getId());
        });
    }

    @Test
    void testAddInvalidMove2() {
        assertThrows(IllegalOperationException.class, () -> {
            openingMoveService.addMove(opening.getId(), null);
        });
    }

    @Test
    void testAddInvalidMove3() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
            entityManager.persist(newMove);
            openingMoveService.addMove(0L, newMove.getId());
        });
    }

    @Test
    void testAddInvalidMove4() {
        assertThrows(IllegalOperationException.class, () -> {
            openingMoveService.addMove(opening.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidMove5() {
        assertThrows(EntityNotFoundException.class, () -> {
            MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
            entityManager.persist(newMove);
            openingMoveService.addMove(321L, newMove.getId());
        });
    }

    @Test
    void testAddInvalidMove6() {
        assertThrows(EntityNotFoundException.class, () -> {
            openingMoveService.addMove(opening.getId(), 321L);
        });
    }

    @Test
    void testGetMoves() throws IllegalOperationException, EntityNotFoundException {
        List<MoveEntity> moveEntities = openingMoveService.getMoves(opening.getId());
        assertEquals(moveEntities.size(), moves.size());
        for (MoveEntity move : moveEntities) {
            assertTrue(opening.getMoves().contains(move));
        }
    }

    @Test
    void testGetInvalidMoves1() {
        assertThrows(IllegalOperationException.class, () -> {
            openingMoveService.getMoves(null);
        });
    }

    @Test
    void testGetInvalidMoves2() {
        assertThrows(IllegalOperationException.class, () -> {
            openingMoveService.getMoves(0L);
        });
    }

    @Test
    void testGetInvalidMoves3() {
        assertThrows(EntityNotFoundException.class, () -> {
            openingMoveService.getMoves(321L);
        });
    }

    @Test
    void testGetMove() throws IllegalOperationException, EntityNotFoundException {
        MoveEntity move = moves.get(0);
        MoveEntity entity = openingMoveService.getMove(move.getId(), opening.getId());
        assertNotNull(entity);
        assertEquals(move.getId(), entity.getId());
    }

    @Test
    void testGetInvalidMove1() {
        assertThrows(IllegalOperationException.class, () -> {
            openingMoveService.getMove(null, opening.getId());
        });
    }

    @Test
    void testGetInvalidMove2() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity move = moves.get(0);
            openingMoveService.getMove(move.getId(), null);
        });
    }

    @Test
    void testGetInvalidMove3() {
        assertThrows(IllegalOperationException.class, () -> {
            openingMoveService.getMove(0L, opening.getId());
        });
    }

    @Test
    void testGetInvalidMove4() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity move = moves.get(0);
            openingMoveService.getMove(move.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidMove5() {
        assertThrows(EntityNotFoundException.class, () -> {
            openingMoveService.getMove(321L, opening.getId());
        });
    }

    @Test
    void testGetInvalidMove6() {
        assertThrows(EntityNotFoundException.class, () -> {
            MoveEntity move = moves.get(0);
            openingMoveService.getMove(move.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidMove7() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
            entityManager.persist(newMove);
            openingMoveService.getMove(newMove.getId(), opening.getId());
        });
    }

    @Test
    void testReplaceMoves() throws IllegalOperationException, EntityNotFoundException {
        List<MoveEntity> newMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MoveEntity move = factory.manufacturePojoWithFullData(MoveEntity.class);
            entityManager.persist(move);
            newMoves.add(move);
        }
        openingMoveService.replaceMoves(opening.getId(), newMoves);
        List<MoveEntity> moveEntities = openingMoveService.getMoves(opening.getId());
        assertEquals(moveEntities.size(), newMoves.size());
        for (MoveEntity move : moveEntities) {
            assertTrue(newMoves.contains(move));
        }
    }

    @Test
    void testReplaceInvalidMoves1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<MoveEntity> newMoves = new ArrayList<>();
            openingMoveService.replaceMoves(null, newMoves);
        });
    }

    @Test
    void testReplaceInvalidMoves2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<MoveEntity> newMoves = new ArrayList<>();
            openingMoveService.replaceMoves(0L, newMoves);
        });
    }

    @Test
    void testReplaceInvalidMoves3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<MoveEntity> newMoves = new ArrayList<>();
            openingMoveService.replaceMoves(321L, newMoves);
        });
    }

    @Test
    void testReplaceInvalidMoves4() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<MoveEntity> newMoves = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                MoveEntity move = factory.manufacturePojoWithFullData(MoveEntity.class);
                entityManager.persist(move);
                newMoves.add(move);
            }
            MoveEntity invalidMove = factory.manufacturePojoWithFullData(MoveEntity.class);
            invalidMove.setId(321L);
            newMoves.add(invalidMove);
            openingMoveService.replaceMoves(opening.getId(), newMoves);
        });
    }

    @Test
    void testRemoveMove() throws IllegalOperationException, EntityNotFoundException {
        for (MoveEntity move : moves) {
            openingMoveService.removeMove(opening.getId(), move.getId());
        }
        assertTrue(openingMoveService.getMoves(opening.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidMove1() {
        assertThrows(IllegalOperationException.class, () -> {
            openingMoveService.removeMove(null, moves.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidMove2() {
        assertThrows(IllegalOperationException.class, () -> {
            openingMoveService.removeMove(opening.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidMove3() {
        assertThrows(IllegalOperationException.class, () -> {
            openingMoveService.removeMove(0L, moves.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidMove4() {
        assertThrows(IllegalOperationException.class, () -> {
            openingMoveService.removeMove(opening.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidMove5() {
        assertThrows(EntityNotFoundException.class, () -> {
            openingMoveService.removeMove(321L, moves.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidMove6() {
        assertThrows(EntityNotFoundException.class, () -> {
            openingMoveService.removeMove(opening.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidMove7() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
            entityManager.persist(newMove);
            openingMoveService.removeMove(opening.getId(), newMove.getId());
        });
    }
}
