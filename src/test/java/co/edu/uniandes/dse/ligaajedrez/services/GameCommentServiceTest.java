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

import co.edu.uniandes.dse.ligaajedrez.entities.CommentEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(GameCommentService.class)
public class GameCommentServiceTest {
    @Autowired
    private GameCommentService gameCommentService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private GameEntity game = new GameEntity();
    private List<CommentEntity> comments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from GameEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from CommentEntity").executeUpdate();
    }

    private void insertData() {
        game = factory.manufacturePojoWithFullData(GameEntity.class);
        entityManager.persist(game);
        for (int i = 0; i < 3; i++) {
            CommentEntity comment = factory.manufacturePojoWithFullData(CommentEntity.class);
            entityManager.persist(comment);
            game.getComments().add(comment);
            comments.add(comment);
        }
    }

    @Test
    void testAddComment() throws IllegalOperationException, EntityNotFoundException {
        CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
        entityManager.persist(newComment);
        gameCommentService.addComment(game.getId(), newComment.getId());
        CommentEntity entity = gameCommentService.getComment(newComment.getId(), game.getId());
        assertEquals(newComment.getId(), entity.getId());
        assertEquals(newComment.getComment(), entity.getComment());
        assertEquals(newComment.getDate(), entity.getDate());
    }

    @Test
    void testAddInvalidComment1() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            entityManager.persist(newComment);
            gameCommentService.addComment(null, newComment.getId());
        });
    }

    @Test
    void testAddInvalidComment2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameCommentService.addComment(game.getId(), null);
        });
    }

    @Test
    void testAddInvalidComment3() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            entityManager.persist(newComment);
            gameCommentService.addComment(0L, newComment.getId());
        });
    }

    @Test
    void testAddInvalidComment4() {
        assertThrows(IllegalOperationException.class, () -> {
            gameCommentService.addComment(game.getId(), 0L);
        });
    }

    @Test
    void testAddInvalidComment5() {
        assertThrows(EntityNotFoundException.class, () -> {
            CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            entityManager.persist(newComment);
            gameCommentService.addComment(321L, newComment.getId());
        });
    }

    @Test
    void testAddInvalidComment6() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameCommentService.addComment(game.getId(), 321L);
        });
    }

    @Test
    void testGetComments() throws IllegalOperationException, EntityNotFoundException {
        List<CommentEntity> commentEntities = gameCommentService.getComments(game.getId());
        assertEquals(commentEntities.size(), comments.size());
        for (CommentEntity comment : commentEntities) {
            assertTrue(game.getComments().contains(comment));
        }
    }

    @Test
    void testGetInvalidComments1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameCommentService.getComments(null);
        });
    }

    @Test
    void testGetInvalidComments2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameCommentService.getComments(0L);
        });
    }

    @Test
    void testGetInvalidComments3() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameCommentService.getComments(321L);
        });
    }

    @Test
    void testGetComment() throws IllegalOperationException, EntityNotFoundException {
        CommentEntity comment = comments.get(0);
        CommentEntity entity = gameCommentService.getComment(comment.getId(), game.getId());
        assertNotNull(entity);
        assertEquals(comment.getId(), entity.getId());
        assertEquals(comment.getComment(), entity.getComment());
        assertEquals(comment.getDate(), entity.getDate());
    }

    @Test
    void testGetInvalidComment1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameCommentService.getComment(null, game.getId());
        });
    }

    @Test
    void testGetInvalidComment2() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity comment = comments.get(0);
            gameCommentService.getComment(comment.getId(), null);
        });
    }

    @Test
    void testGetInvalidComment3() {
        assertThrows(IllegalOperationException.class, () -> {
            gameCommentService.getComment(0L, game.getId());
        });
    }

    @Test
    void testGetInvalidComment4() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity comment = comments.get(0);
            gameCommentService.getComment(comment.getId(), 0L);
        });
    }

    @Test
    void testGetInvalidComment5() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameCommentService.getComment(321L, game.getId());
        });
    }

    @Test
    void testGetInvalidComment6() {
        assertThrows(EntityNotFoundException.class, () -> {
            CommentEntity comment = comments.get(0);
            gameCommentService.getComment(comment.getId(), 321L);
        });
    }

    @Test
    void testGetInvalidComment7() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            entityManager.persist(newComment);
            gameCommentService.getComment(newComment.getId(), game.getId());
        });
    }

    @Test
    void testReplaceComments() throws IllegalOperationException, EntityNotFoundException {
        List<CommentEntity> newComments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CommentEntity comment = factory.manufacturePojoWithFullData(CommentEntity.class);
            entityManager.persist(comment);
            newComments.add(comment);
        }
        gameCommentService.replaceComments(game.getId(), newComments);
        List<CommentEntity> commentEntities = gameCommentService.getComments(game.getId());
        assertEquals(commentEntities.size(), newComments.size());
        for (CommentEntity comment : commentEntities) {
            assertTrue(newComments.contains(comment));
        }
    }

    @Test
    void testReplaceInvalidComments1() {
        assertThrows(IllegalOperationException.class, () -> {
            List<CommentEntity> newComments = new ArrayList<>();
            gameCommentService.replaceComments(null, newComments);
        });
    }

    @Test
    void testReplaceInvalidComments2() {
        assertThrows(IllegalOperationException.class, () -> {
            List<CommentEntity> newComments = new ArrayList<>();
            gameCommentService.replaceComments(0L, newComments);
        });
    }

    @Test
    void testReplaceInvalidComments3() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<CommentEntity> newComments = new ArrayList<>();
            gameCommentService.replaceComments(321L, newComments);
        });
    }

    @Test
    void testReplaceInvalidComments4() {
        assertThrows(EntityNotFoundException.class, () -> {
            List<CommentEntity> newComments = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                CommentEntity comment = factory.manufacturePojoWithFullData(CommentEntity.class);
                entityManager.persist(comment);
                newComments.add(comment);
            }
            CommentEntity invalidComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            invalidComment.setId(321L);
            newComments.add(invalidComment);
            gameCommentService.replaceComments(game.getId(), newComments);
        });
    }

    @Test
    void testRemoveComment() throws IllegalOperationException, EntityNotFoundException {
        for (CommentEntity comment : comments) {
            gameCommentService.removeComment(game.getId(), comment.getId());
        }
        assertTrue(gameCommentService.getComments(game.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidComment1() {
        assertThrows(IllegalOperationException.class, () -> {
            gameCommentService.removeComment(null, comments.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidComment2() {
        assertThrows(IllegalOperationException.class, () -> {
            gameCommentService.removeComment(game.getId(), null);
        });
    }

    @Test
    void testRemoveInvalidComment3() {
        assertThrows(IllegalOperationException.class, () -> {
            gameCommentService.removeComment(0L, comments.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidComment4() {
        assertThrows(IllegalOperationException.class, () -> {
            gameCommentService.removeComment(game.getId(), 0L);
        });
    }

    @Test
    void testRemoveInvalidComment5() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameCommentService.removeComment(321L, comments.get(0).getId());
        });
    }

    @Test
    void testRemoveInvalidComment6() {
        assertThrows(EntityNotFoundException.class, () -> {
            gameCommentService.removeComment(game.getId(), 321L);
        });
    }

    @Test
    void testRemoveInvalidComment7() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            entityManager.persist(newComment);
            gameCommentService.removeComment(game.getId(), newComment.getId());
        });
    }
}
