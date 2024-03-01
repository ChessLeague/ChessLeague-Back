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

import co.edu.uniandes.dse.ligaajedrez.entities.AdministratorEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.CommentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({CommentAdministratorService.class, AdministratorService.class, CommentService.class})
public class CommentAdministratorServiceTest {
    @Autowired
    private CommentAdministratorService commentAdministratorService;

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<AdministratorEntity> administrators = new ArrayList<>();
    private List<CommentEntity> comments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AdministratorEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from CommentEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            AdministratorEntity administrator = factory.manufacturePojoWithFullData(AdministratorEntity.class);
            administrator.setIDNumber(Integer.toString(i));
            entityManager.persist(administrator);
            administrators.add(administrator);
        }
        for (int i = 0; i < 3; i++) {
            CommentEntity comment = factory.manufacturePojoWithFullData(CommentEntity.class);
            entityManager.persist(comment);
            comments.add(comment);
        }
        comments.get(0).setAuthor(administrators.get(0));
    }

    @Test
    void testAddAdministrator() throws IllegalOperationException, EntityNotFoundException {
        CommentEntity comment = comments.get(1);
        AdministratorEntity administrator = administrators.get(1);
        commentAdministratorService.addAdministrator(comment.getId(), administrator.getId());
        AdministratorEntity entity = administratorService.getAdministrator(administrator.getId());
        assertEquals(administrator.getId(), entity.getId());
        assertEquals(administrator.getName(), entity.getName());
        assertEquals(administrator.getPhotoURL(), entity.getPhotoURL());
        assertEquals(administrator.getIDNumber(), entity.getIDNumber());
        assertEquals(administrator.getUsername(), entity.getUsername());
        assertEquals(administrator.getPassword(), entity.getPassword());
    }

    @Test
    void testAddInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.addAdministrator(null, administrators.get(1).getId());
        });
    }

    @Test
    void testAddInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.addAdministrator(comments.get(1).getId(), null);
        });
    }

    @Test
    void testAddInvalidAdministrator3() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.addAdministrator(0L, administrators.get(1).getId());
        });
    }

    @Test
    void testAddInvalidAdministrator4() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.addAdministrator(comments.get(1).getId(), 0L);
        });
    }

    @Test
    void testAddInvalidAdministrator5() {
        assertThrows(EntityNotFoundException.class, () -> {
            commentAdministratorService.addAdministrator(321L, administrators.get(1).getId());
        });
    }

    @Test
    void testAddInvalidAdministrator6() {
        assertThrows(EntityNotFoundException.class, () -> {
            commentAdministratorService.addAdministrator(comments.get(1).getId(), 321L);
        });
    }

    @Test
    void testGetAdministrator() throws IllegalOperationException, EntityNotFoundException {
        AdministratorEntity administrator = commentAdministratorService.getAdministrator(comments.get(0).getId());
        AdministratorEntity entity = administratorService.getAdministrator(administrators.get(0).getId());
        assertEquals(administrator.getId(), entity.getId());
    }

    @Test
    void testGetInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.getAdministrator(null);
        });
    }

    @Test
    void testGetInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.getAdministrator(0L);
        });
    }

    @Test
    void testGetInvalidAdministrator3() {
        assertThrows(EntityNotFoundException.class, () -> {
            commentAdministratorService.getAdministrator(321L);
        });
    }

    @Test
    void testReplaceAdministrator() throws IllegalOperationException, EntityNotFoundException {
        CommentEntity comment = comments.get(0);
        AdministratorEntity administrator = administrators.get(1);
        commentAdministratorService.addAdministrator(comment.getId(), administrator.getId());
        AdministratorEntity entity = administratorService.getAdministrator(administrator.getId());
        assertEquals(administrator.getId(), entity.getId());
        assertEquals(administrator.getName(), entity.getName());
        assertEquals(administrator.getPhotoURL(), entity.getPhotoURL());
        assertEquals(administrator.getIDNumber(), entity.getIDNumber());
        assertEquals(administrator.getUsername(), entity.getUsername());
        assertEquals(administrator.getPassword(), entity.getPassword());
    }

    @Test
    void testReplaceInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.replaceAdministrator(null, administrators.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.replaceAdministrator(comments.get(0).getId(), null);
        });
    }

    @Test
    void testReplaceInvalidAdministrator3() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.replaceAdministrator(0L, administrators.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidAdministrator4() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.replaceAdministrator(comments.get(0).getId(), 0L);
        });
    }

    @Test
    void testReplaceInvalidAdministrator5() {
        assertThrows(EntityNotFoundException.class, () -> {
            commentAdministratorService.replaceAdministrator(321L, administrators.get(0).getId());
        });
    }

    @Test
    void testReplaceInvalidAdministrator6() {
        assertThrows(EntityNotFoundException.class, () -> {
            commentAdministratorService.replaceAdministrator(comments.get(0).getId(), 321L);
        });
    }

    @Test
    void testRemoveAdministrator() throws IllegalOperationException, EntityNotFoundException {
        commentAdministratorService.removeAdministrator(comments.get(0).getId());
        CommentEntity entity = commentService.getComment(comments.get(0).getId());
        assertNull(entity.getAuthor());
    }

    @Test
    void testRemoveInvalidAdministrator1() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.removeAdministrator(null);
        });
    }

    @Test
    void testRemoveInvalidAdministrator2() {
        assertThrows(IllegalOperationException.class, () -> {
            commentAdministratorService.removeAdministrator(0L);
        });
    }

    @Test
    void testRemoveInvalidAdministrator3() {
        assertThrows(EntityNotFoundException.class, () -> {
            commentAdministratorService.removeAdministrator(321L);
        });
    }
}
