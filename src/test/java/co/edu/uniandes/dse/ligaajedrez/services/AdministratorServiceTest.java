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

import co.edu.uniandes.dse.ligaajedrez.entities.AdministratorEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdministratorService.class)
public class AdministratorServiceTest {
    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<AdministratorEntity> administratorList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AdministratorEntity").executeUpdate();
    }

    private void insertData() {
        for(int i = 0; i < 3; i++) {
            AdministratorEntity administratorEntity = factory.manufacturePojo(AdministratorEntity.class);
            administratorEntity.setIDNumber(Integer.toString(i));
            administratorEntity.setUsername(Integer.toString(i));
            entityManager.persist(administratorEntity);
            administratorList.add(administratorEntity);
        }
    }

    @Test
    void testCreateAdministrator() throws IllegalOperationException {
        AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
        newAdministrator.setId(123L);
        newAdministrator.setName("Test Name");
        newAdministrator.setPhotoURL("Test Photo URL");
        newAdministrator.setIDNumber("Test ID Number");
        newAdministrator.setUsername("Test Username");
        newAdministrator.setPassword("Test Password");
        AdministratorEntity administratorEntity = administratorService.createAdministrator(newAdministrator);
        assertNotNull(administratorEntity);
        AdministratorEntity entity = entityManager.find(AdministratorEntity.class, administratorEntity.getId());
        assertEquals(administratorEntity.getId(), entity.getId());
        assertEquals(administratorEntity.getName(), entity.getName());
        assertEquals(administratorEntity.getPhotoURL(), entity.getPhotoURL());
        assertEquals(administratorEntity.getIDNumber(), entity.getIDNumber());
        assertEquals(administratorEntity.getUsername(), entity.getUsername());
        assertEquals(administratorEntity.getPassword(), entity.getPassword());
    }

    @Test
    void testCreateInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setId(null);
            newAdministrator.setName("Test Name");
            newAdministrator.setPhotoURL("Test Photo URL");
            newAdministrator.setIDNumber("Test ID Number");
            newAdministrator.setUsername("Test Username");
            newAdministrator.setPassword("Test Password");
            administratorService.createAdministrator(newAdministrator);
        });
    }

    @Test
    void testCreateInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setId(0L);
            newAdministrator.setName("Test Name");
            newAdministrator.setPhotoURL("Test Photo URL");
            newAdministrator.setIDNumber("Test ID Number");
            newAdministrator.setUsername("Test Username");
            newAdministrator.setPassword("Test Password");
            administratorService.createAdministrator(newAdministrator);
        });
    }

    @Test
    void testCreateInvalidAdministrator3() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setId(123L);
            newAdministrator.setName(null);
            newAdministrator.setPhotoURL("Test Photo URL");
            newAdministrator.setIDNumber("Test ID Number");
            newAdministrator.setUsername("Test Username");
            newAdministrator.setPassword("Test Password");
            administratorService.createAdministrator(newAdministrator);
        });
    }

    @Test
    void testCreateInvalidAdministrator4() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setId(123L);
            newAdministrator.setName("Test Name");
            newAdministrator.setPhotoURL(null);
            newAdministrator.setIDNumber("Test ID Number");
            newAdministrator.setUsername("Test Username");
            newAdministrator.setPassword("Test Password");
            administratorService.createAdministrator(newAdministrator);
        });
    }

    @Test
    void testCreateInvalidAdministrator5() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setId(123L);
            newAdministrator.setName("Test Name");
            newAdministrator.setPhotoURL("Test Photo URL");
            newAdministrator.setIDNumber(null);
            newAdministrator.setUsername("Test Username");
            newAdministrator.setPassword("Test Password");
            administratorService.createAdministrator(newAdministrator);
        });
    }

    @Test
    void testCreateInvalidAdministrator6() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setId(123L);
            newAdministrator.setName("Test Name");
            newAdministrator.setPhotoURL("Test Photo URL");
            newAdministrator.setIDNumber(administratorList.get(0).getIDNumber());
            newAdministrator.setUsername("Test Username");
            newAdministrator.setPassword("Test Password");
            administratorService.createAdministrator(newAdministrator);
        });
    }

    @Test
    void testCreateInvalidAdministrator7() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setId(123L);
            newAdministrator.setName("Test Name");
            newAdministrator.setPhotoURL("Test Photo URL");
            newAdministrator.setIDNumber("Test ID Number");
            newAdministrator.setUsername(null);
            newAdministrator.setPassword("Test Password");
            administratorService.createAdministrator(newAdministrator);
        });
    }

    @Test
    void testCreateInvalidAdministrator8() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setUsername(administratorList.get(0).getUsername());
            newAdministrator.setId(123L);
            newAdministrator.setName("Test Name");
            newAdministrator.setPhotoURL("Test Photo URL");
            newAdministrator.setIDNumber("Test ID Number");
            newAdministrator.setUsername(administratorList.get(0).getUsername());
            newAdministrator.setPassword("Test Password");
            administratorService.createAdministrator(newAdministrator);
        });
    }

    @Test
    void testCreateInvalidAdministrator9() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity newAdministrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            newAdministrator.setId(123L);
            newAdministrator.setName("Test Name");
            newAdministrator.setPhotoURL("Test Photo URL");
            newAdministrator.setIDNumber("Test ID Number");
            newAdministrator.setUsername("Test Username");
            newAdministrator.setPassword(null);
            administratorService.createAdministrator(newAdministrator);
        });
    }

    @Test
    void testGetAdministrators() {
        List<AdministratorEntity> administrators = administratorService.getAdministrators();
        assertEquals(administrators.size(), administratorList.size());
        for(AdministratorEntity administrator : administrators) {
            boolean found = false;
            for(AdministratorEntity storedAdministrator : administratorList) {
                if(administrator.getId().equals(storedAdministrator.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void testGetAdministrator() throws IllegalOperationException, EntityNotFoundException {
        AdministratorEntity storedAdministrator = administratorList.get(0);
        AdministratorEntity administrator = administratorService.getAdministrator(storedAdministrator.getId());
        assertNotNull(administrator);
        assertEquals(storedAdministrator.getId(), administrator.getId());
        assertEquals(storedAdministrator.getName(), administrator.getName());
        assertEquals(storedAdministrator.getPhotoURL(), administrator.getPhotoURL());
        assertEquals(storedAdministrator.getIDNumber(), administrator.getIDNumber());
        assertEquals(storedAdministrator.getUsername(), administrator.getUsername());
        assertEquals(storedAdministrator.getPassword(), administrator.getPassword());
    }

    @Test
    void testGetInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorService.getAdministrator(null);
        });
    }

    @Test
    void testGetInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorService.getAdministrator(0L);
        });
    }

    @Test
    void testGetInvalidAdministrator3() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.getAdministrator(321L);
        });
    }

    @Test
    void testUpdateAdministrator() throws IllegalOperationException, EntityNotFoundException {
        AdministratorEntity storedAdministrator = administratorList.get(0);
        AdministratorEntity administrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
        administrator.setId(storedAdministrator.getId());
        administrator.setName("Test Name");
        administrator.setPhotoURL("Test Photo URL");
        administrator.setIDNumber("Test ID Number");
        administrator.setUsername("Test Username");
        administrator.setPassword("Test Password");
        administratorService.updateAdministrator(storedAdministrator.getId(), administrator);
        AdministratorEntity updatedAdministrator = entityManager.find(AdministratorEntity.class, storedAdministrator.getId());
        assertEquals(administrator.getId(), updatedAdministrator.getId());
        assertEquals(administrator.getName(), updatedAdministrator.getName());
        assertEquals(administrator.getPhotoURL(), updatedAdministrator.getPhotoURL());
        assertEquals(administrator.getIDNumber(), updatedAdministrator.getIDNumber());
        assertEquals(administrator.getUsername(), updatedAdministrator.getUsername());
        assertEquals(administrator.getPassword(), updatedAdministrator.getPassword());
    }

    @Test
    void testUpdateInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity storedAdministrator = administratorList.get(0);
            administratorService.updateAdministrator(null, storedAdministrator);
        });
    }

    @Test
    void testUpdateInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity storedAdministrator = administratorList.get(0);
            administratorService.updateAdministrator(0L, storedAdministrator);
        });
    }

    @Test
    void testUpdateInvalidAdministrator3() {
        assertThrows(EntityNotFoundException.class, () -> {
            AdministratorEntity storedAdministrator = administratorList.get(0);
            administratorService.updateAdministrator(321L, storedAdministrator);
        });
    }

    @Test
    void testUpdateInvalidAdministrator4() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity storedAdministrator = administratorList.get(0);
            storedAdministrator.setName(null);
            storedAdministrator.setPhotoURL("Test Photo URL");
            storedAdministrator.setIDNumber("Test ID Number");
            storedAdministrator.setUsername("Test Username");
            storedAdministrator.setPassword("Test Password");
            administratorService.updateAdministrator(storedAdministrator.getId(), storedAdministrator);
        });
    }

    @Test
    void testUpdateInvalidAdministrator5() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity storedAdministrator = administratorList.get(0);
            storedAdministrator.setName("Test Name");
            storedAdministrator.setPhotoURL(null);
            storedAdministrator.setIDNumber("Test ID Number");
            storedAdministrator.setUsername("Test Username");
            storedAdministrator.setPassword("Test Password");
            administratorService.updateAdministrator(storedAdministrator.getId(), storedAdministrator);
        });
    }
    
    @Test
    void testUpdateInvalidAdministrator6() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity storedAdministrator = administratorList.get(0);
            storedAdministrator.setName("Test Name");
            storedAdministrator.setPhotoURL("Test Photo URL");
            storedAdministrator.setIDNumber(null);
            storedAdministrator.setUsername("Test Username");
            storedAdministrator.setPassword("Test Password");
            administratorService.updateAdministrator(storedAdministrator.getId(), storedAdministrator);
        });
    }

    @Test
    void testUpdateInvalidAdministrator7() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity storedAdministrator = administratorList.get(0);
            storedAdministrator.setName("Test Name");
            storedAdministrator.setPhotoURL("Test Photo URL");
            storedAdministrator.setIDNumber(administratorList.get(1).getIDNumber());
            storedAdministrator.setUsername("Test Username");
            storedAdministrator.setPassword("Test Password");
            administratorService.updateAdministrator(storedAdministrator.getId(), storedAdministrator);
        });
    }

    @Test
    void testUpdateInvalidAdministrator8() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity storedAdministrator = administratorList.get(0);
            storedAdministrator.setName("Test Name");
            storedAdministrator.setPhotoURL("Test Photo URL");
            storedAdministrator.setIDNumber("Test ID Number");
            storedAdministrator.setUsername(null);
            storedAdministrator.setPassword("Test Password");
            administratorService.updateAdministrator(storedAdministrator.getId(), storedAdministrator);
        });
    }

    @Test
    void testUpdateInvalidAdministrator9() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity storedAdministrator = administratorList.get(0);
            storedAdministrator.setName("Test Name");
            storedAdministrator.setPhotoURL("Test Photo URL");
            storedAdministrator.setIDNumber("Test ID Number");
            storedAdministrator.setUsername(administratorList.get(1).getUsername());
            storedAdministrator.setPassword("Test Password");
            administratorService.updateAdministrator(storedAdministrator.getId(), storedAdministrator);
        });
    }

    @Test
    void testUpdateInvalidAdministrator10() {
        assertThrows(IllegalOperationException.class, () -> {
            AdministratorEntity storedAdministrator = administratorList.get(0);
            storedAdministrator.setName("Test Name");
            storedAdministrator.setPhotoURL("Test Photo URL");
            storedAdministrator.setIDNumber("Test ID Number");
            storedAdministrator.setUsername("Test Username");
            storedAdministrator.setPassword(null);
            administratorService.updateAdministrator(storedAdministrator.getId(), storedAdministrator);
        });
    }

    @Test
    void testDeleteAdministrator() throws IllegalOperationException, EntityNotFoundException {
        AdministratorEntity storedAdministrator = administratorList.get(0);
        administratorService.deleteAdministrator(storedAdministrator.getId());
        AdministratorEntity deletedAdministrator = entityManager.find(AdministratorEntity.class, storedAdministrator.getId());
        assertNull(deletedAdministrator);
    }

    @Test
    void testDeleteInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorService.deleteAdministrator(null);
        });
    }

    @Test
    void testDeleteInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorService.deleteAdministrator(0L);
        });
    }

    @Test
    void testDeleteInvalidAdministrator3() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.deleteAdministrator(321L);
        });
    }
}
