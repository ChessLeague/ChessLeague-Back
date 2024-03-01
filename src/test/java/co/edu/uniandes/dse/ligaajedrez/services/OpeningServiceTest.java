package co.edu.uniandes.dse.ligaajedrez.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import co.edu.uniandes.dse.ligaajedrez.entities.OpeningEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.OpeningType;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(OpeningService.class)
public class OpeningServiceTest {
    @Autowired
    private OpeningService openingService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<OpeningEntity> openingList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from OpeningEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            OpeningEntity openingEntity = factory.manufacturePojo(OpeningEntity.class);
            openingEntity.setName("Test Name");
            openingEntity.setClassification(OpeningType.ABIERTA);
            entityManager.persist(openingEntity);
            openingList.add(openingEntity);
        }
    }

    @Test
    void testCreateOpening() throws IllegalOperationException {
        OpeningEntity newOpening = factory.manufacturePojoWithFullData(OpeningEntity.class);
        newOpening.setId(123L);
        newOpening.setName("Test Name");
        newOpening.setClassification(OpeningType.ABIERTA);
        OpeningEntity openingEntity = openingService.createOpening(newOpening);
        assertNotNull(openingEntity);
        OpeningEntity entity = entityManager.find(OpeningEntity.class, openingEntity.getId());
        assertEquals(openingEntity.getId(), entity.getId());
        assertEquals(openingEntity.getName(), entity.getName());
        assertEquals(openingEntity.getClassification(), entity.getClassification());
    }

    @Test
    void testCreateInvalidOpening1() {
        assertThrows(IllegalOperationException.class, () -> {
            OpeningEntity newOpening = factory.manufacturePojoWithFullData(OpeningEntity.class);
            newOpening.setId(null);
            newOpening.setClassification(OpeningType.ABIERTA);
            openingService.createOpening(newOpening);
        });
    }

    @Test
    void testCreateInvalidOpening2() {
        assertThrows(IllegalOperationException.class, () -> {
            OpeningEntity newOpening = factory.manufacturePojoWithFullData(OpeningEntity.class);
            newOpening.setId(0L);
            newOpening.setClassification(OpeningType.ABIERTA);
            openingService.createOpening(newOpening);
        });
    }

    @Test
    void testCreateInvalidOpening3() {
        assertThrows(IllegalOperationException.class, () -> {
            OpeningEntity newOpening = factory.manufacturePojoWithFullData(OpeningEntity.class);
            newOpening.setId(123L);
            newOpening.setClassification(null);
            openingService.createOpening(newOpening);
        });
    }

    @Test
    void testGetOpenings() {
        List<OpeningEntity> openings = openingService.getOpenings();
        assertEquals(openings.size(), openingList.size());
        for (OpeningEntity opening : openings) {
            boolean found = false;
            for (OpeningEntity storedOpening : openingList) {
                if (opening.getId().equals(storedOpening.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void testGetOpening() throws IllegalOperationException, EntityNotFoundException {
        OpeningEntity storedOpening = openingList.get(0);
        OpeningEntity opening = openingService.getOpening(storedOpening.getId());
        assertNotNull(opening);
        assertEquals(storedOpening.getId(), opening.getId());
        assertEquals(storedOpening.getName(), opening.getName());
        assertEquals(storedOpening.getClassification(), opening.getClassification());
    }

    @Test
    void testGetInvalidOpening1() {
        assertThrows(IllegalOperationException.class, () -> {
            openingService.getOpening(null);
        });
    }

    @Test
    void testGetInvalidOpening2() {
        assertThrows(IllegalOperationException.class, () -> {
            openingService.getOpening(0L);
        });
    }

    @Test
    void testGetInvalidOpening3() {
        assertThrows(EntityNotFoundException.class, () -> {
            openingService.getOpening(321L);
        });
    }

    @Test
    void testUpdateOpening() throws IllegalOperationException, EntityNotFoundException {
        OpeningEntity storedOpening = openingList.get(0);
        OpeningEntity opening = new OpeningEntity();
        opening.setId(storedOpening.getId());
        opening.setName("Test Name");
        opening.setClassification(OpeningType.ABIERTA);
        OpeningEntity updatedOpening = openingService.updateOpening(storedOpening.getId(), opening);
        assertEquals(opening.getId(), updatedOpening.getId());
        assertEquals(opening.getName(), updatedOpening.getName());
    }

    @Test
    void testUpdateInvalidOpening1() {
        assertThrows(IllegalOperationException.class, () -> {
            OpeningEntity storedOpening = openingList.get(0);
            openingService.updateOpening(null, storedOpening);
        });
    }

    @Test
    void testUpdateInvalidOpening2() {
        assertThrows(IllegalOperationException.class, () -> {
            OpeningEntity storedOpening = openingList.get(0);
            openingService.updateOpening(0L, storedOpening);
        });
    }

    @Test
    void testUpdateInvalidOpening3() {
        assertThrows(EntityNotFoundException.class, () -> {
            openingService.updateOpening(321L, openingList.get(0));
        });
    }

    @Test
    void testUpdateInvalidOpening4() {
        assertThrows(IllegalOperationException.class, () -> {
            OpeningEntity storedOpening = openingList.get(0);
            storedOpening.setClassification(null);
            openingService.updateOpening(storedOpening.getId(), storedOpening);
        });
    }

    @Test
    void testDeleteOpening() throws IllegalOperationException, EntityNotFoundException {
        OpeningEntity storedOpening = openingList.get(0);
        openingService.deleteOpening(storedOpening.getId());
        OpeningEntity deletedOpening = entityManager.find(OpeningEntity.class, storedOpening.getId());
        assertNull(deletedOpening);
    }

    @Test
    void testDeleteInvalidOpening1() {
        assertThrows(IllegalOperationException.class, () -> {
            openingService.deleteOpening(null);
        });
    }

    @Test
    void testDeleteInvalidOpening2() {
        assertThrows(IllegalOperationException.class, () -> {
            openingService.deleteOpening(0L);
        });
    }

    @Test
    void testDeleteInvalidOpening3() {
        assertThrows(EntityNotFoundException.class, () -> {
            openingService.deleteOpening(321L);
        });
    }
}
