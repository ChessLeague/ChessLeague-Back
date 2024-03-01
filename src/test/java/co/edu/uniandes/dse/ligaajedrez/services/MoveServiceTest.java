package co.edu.uniandes.dse.ligaajedrez.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import co.edu.uniandes.dse.ligaajedrez.entities.MoveEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(MoveService.class)
public class MoveServiceTest {
    @Autowired
    private MoveService moveService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<MoveEntity> moveList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from MoveEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            MoveEntity moveEntity = factory.manufacturePojoWithFullData(MoveEntity.class);
            moveEntity.setNotation("e4");
            entityManager.persist(moveEntity);
            moveList.add(moveEntity);
        }
    }

    @Test
    void testCreateMove() throws IllegalOperationException {
        MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
        newMove.setId(123L);
        newMove.setNotation("d4");
        MoveEntity moveEntity = moveService.createMove(newMove);
        assertNotNull(moveEntity);
        MoveEntity entity = entityManager.find(MoveEntity.class, moveEntity.getId());
        assertEquals(moveEntity.getId(), entity.getId());
        assertEquals(moveEntity.getNotation(), entity.getNotation());
    }

    @Test
    void testCreateInvalidMove1() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
            newMove.setId(null);
            newMove.setNotation("d4");
            moveService.createMove(newMove);
        });
    }

    @Test
    void testCreateInvalidMove2() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
            newMove.setId(0L);
            newMove.setNotation("d4");
            moveService.createMove(newMove);
        });
    }

    @Test
    void testCreateInvalidMove3() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
            newMove.setId(123L);
            newMove.setNotation(null);
            moveService.createMove(newMove);
        });
    }

    @Test
    void testCreateInvalidMove4() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity newMove = factory.manufacturePojoWithFullData(MoveEntity.class);
            newMove.setId(123L);
            newMove.setNotation("invalidNotation");
            moveService.createMove(newMove);
        });
    }

    @Test
    void testGetMoves() {
        List<MoveEntity> moves = moveService.getMoves();
        assertEquals(moves.size(), moveList.size());
        for (MoveEntity move : moves) {
            boolean found = false;
            for (MoveEntity storedMove : moveList) {
                if (move.getId().equals(storedMove.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void testGetMove() throws IllegalOperationException, EntityNotFoundException {
        MoveEntity storedMove = moveList.get(0);
        MoveEntity move = moveService.getMove(storedMove.getId());
        assertNotNull(move);
        assertEquals(storedMove.getId(), move.getId());
        assertEquals(storedMove.getNotation(), move.getNotation());
    }

    @Test
    void testGetInvalidMove1() {
        assertThrows(IllegalOperationException.class, () -> {
            moveService.getMove(null);
        });
    }

    @Test
    void testGetInvalidMove2() {
        assertThrows(IllegalOperationException.class, () -> {
            moveService.getMove(0L);
        });
    }

    @Test
    void testGetInvalidMove3() {
        assertThrows(EntityNotFoundException.class, () -> {
            moveService.getMove(321L);
        });
    }

    @Test
    void testUpdateMove() throws IllegalOperationException, EntityNotFoundException {
        MoveEntity storedMove = moveList.get(0);
        MoveEntity move = factory.manufacturePojoWithFullData(MoveEntity.class);
        move.setId(storedMove.getId());
        move.setNotation("Nf3");
        MoveEntity updatedMove = moveService.updateMove(storedMove.getId(), move);
        assertEquals(move.getId(), updatedMove.getId());
        assertEquals(move.getNotation(), updatedMove.getNotation());
    }

    @Test
    void testUpdateInvalidMove1() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity storedMove = moveList.get(0);
            moveService.updateMove(null, storedMove);
        });
    }

    @Test
    void testUpdateInvalidMove2() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity storedMove = moveList.get(0);
            moveService.updateMove(0L, storedMove);
        });
    }

    @Test
    void testUpdateInvalidMove3() {
        assertThrows(EntityNotFoundException.class, () -> {
            moveService.updateMove(321L, moveList.get(0));
        });
    }

    @Test
    void testUpdateInvalidMove4() {
        assertThrows(IllegalOperationException.class, () -> {
            MoveEntity storedMove = moveList.get(0);
            storedMove.setNotation(null);
            moveService.updateMove(storedMove.getId(), storedMove);
        });
    }

    @Test
    void testDeleteMove() throws IllegalOperationException, EntityNotFoundException {
        MoveEntity storedMove = moveList.get(0);
        moveService.deleteMove(storedMove.getId());
        MoveEntity deletedMove = entityManager.find(MoveEntity.class, storedMove.getId());
        assertNull(deletedMove);
    }

    @Test
    void testDeleteInvalidMove1() {
        assertThrows(IllegalOperationException.class, () -> {
            moveService.deleteMove(null);
        });
    }

    @Test
    void testDeleteInvalidMove2() {
        assertThrows(IllegalOperationException.class, () -> {
            moveService.deleteMove(0L);
        });
    }

    @Test
    void testDeleteInvalidMove3() {
        assertThrows(EntityNotFoundException.class, () -> {
            moveService.deleteMove(321L);
        });
    }

    @Test
    void testIsValidChessNotation() {
        assertTrue(moveService.isValidChessNotation("e4"));
        assertTrue(moveService.isValidChessNotation("Nf3"));
        assertTrue(moveService.isValidChessNotation("O-O"));
        assertTrue(moveService.isValidChessNotation("O-O+"));
        assertTrue(moveService.isValidChessNotation("O-O#"));
        assertTrue(moveService.isValidChessNotation("O-O-O"));
        assertTrue(moveService.isValidChessNotation("O-O-O#"));
        assertTrue(moveService.isValidChessNotation("Qxe5+"));
        assertTrue(moveService.isValidChessNotation("Kf7"));
        assertTrue(moveService.isValidChessNotation("Rf1#"));
        assertFalse(moveService.isValidChessNotation("invalidNotation"));
        assertFalse(moveService.isValidChessNotation("e9"));
    }
}
