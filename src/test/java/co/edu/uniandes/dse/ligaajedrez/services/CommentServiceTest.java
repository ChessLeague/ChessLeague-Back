package co.edu.uniandes.dse.ligaajedrez.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.CommentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(CommentService.class)
public class CommentServiceTest {
    @Autowired
    private CommentService commentService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<CommentEntity> commentList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from CommentEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            CommentEntity commentEntity = factory.manufacturePojo(CommentEntity.class);
            commentEntity.setDate(new Date());
            entityManager.persist(commentEntity);
            commentList.add(commentEntity);
        }
    }

    @Test
    void testCreateComment() throws IllegalOperationException {
        CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
        newComment.setId(123L);
        newComment.setComment("Test Comment");
        newComment.setDate(new Date());
        CommentEntity commentEntity = commentService.createComment(newComment);
        assertNotNull(commentEntity);
        CommentEntity entity = entityManager.find(CommentEntity.class, commentEntity.getId());
        assertEquals(commentEntity.getId(), entity.getId());
        assertEquals(commentEntity.getComment(), entity.getComment());
        assertEquals(commentEntity.getDate(), entity.getDate());
        assertEquals(commentEntity.getAuthor(), entity.getAuthor());
    }

    @Test
    void testCreateInvalidComment1() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            newComment.setId(null);
            newComment.setComment("Test Comment");
            newComment.setDate(new Date());
            commentService.createComment(newComment);
        });
    }

    @Test
    void testCreateInvalidComment2() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            newComment.setId(0L);
            newComment.setComment("Test Comment");
            newComment.setDate(new Date());
            commentService.createComment(newComment);
        });
    }

    @Test
    void testCreateInvalidComment3() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            newComment.setId(123L);
            newComment.setComment(null);
            newComment.setDate(new Date());
            commentService.createComment(newComment);
        });
    }

    @Test
    void testCreateInvalidComment4() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            newComment.setId(123L);
            newComment.setComment("Test Comment");
            newComment.setDate(null);
            commentService.createComment(newComment);
        });
    }

    @Test
    void testCreateInvalidComment5() {
        assertThrows(IllegalOperationException.class, () -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date()); 
            calendar.add(Calendar.YEAR, 1);
            CommentEntity newComment = factory.manufacturePojoWithFullData(CommentEntity.class);
            newComment.setId(123L);
            newComment.setComment("Test Comment");
            newComment.setDate(calendar.getTime());
            commentService.createComment(newComment);
        });
    }

    @Test
    void testGetComments() {
        List<CommentEntity> comments = commentService.getComments();
        assertEquals(comments.size(), commentList.size());
        for (CommentEntity comment : comments) {
            boolean found = false;
            for (CommentEntity storedComment : commentList) {
                if (comment.getId().equals(storedComment.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void testGetComment() throws IllegalOperationException, EntityNotFoundException {
        CommentEntity storedComment = commentList.get(0);
        CommentEntity comment = commentService.getComment(storedComment.getId());
        assertNotNull(comment);
        assertEquals(storedComment.getId(), comment.getId());
        assertEquals(storedComment.getComment(), comment.getComment());
        assertEquals(storedComment.getDate(), comment.getDate());
    }

    @Test
    void testGetInvalidComment1() {
        assertThrows(IllegalOperationException.class, () -> {
            commentService.getComment(null);
        });
    }

    @Test
    void testGetInvalidComment2() {
        assertThrows(IllegalOperationException.class, () -> {
            commentService.getComment(0L);
        });
    }

    @Test
    void testGetInvalidComment3() {
        assertThrows(EntityNotFoundException.class, () -> {
            commentService.getComment(321L);
        });
    }

    @Test
    void testUpdateComment() throws IllegalOperationException, EntityNotFoundException {
        CommentEntity storedComment = commentList.get(0);
        CommentEntity comment = factory.manufacturePojoWithFullData(CommentEntity.class);
        comment.setId(storedComment.getId());
        comment.setComment("Updated Comment");
        comment.setDate(new Date());
        commentService.updateComment(storedComment.getId(), comment);
        CommentEntity updatedComment = entityManager.find(CommentEntity.class, storedComment.getId());
        assertEquals(comment.getId(), updatedComment.getId());
        assertEquals(comment.getComment(), updatedComment.getComment());
        assertEquals(comment.getDate(), updatedComment.getDate());
    }

    @Test
    void testUpdateInvalidComment1() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity storedComment = commentList.get(0);
            commentService.updateComment(null, storedComment);
        });
    }

    @Test
    void testUpdateInvalidComment2() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity storedComment = commentList.get(0);
            commentService.updateComment(0L, storedComment);
        });
    }

    @Test
    void testUpdateInvalidComment3() {
        assertThrows(EntityNotFoundException.class, () -> {
            CommentEntity storedComment = commentList.get(0);
            commentService.updateComment(321L, storedComment);
        });
    }

    @Test
    void testUpdateInvalidComment4() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity storedComment = commentList.get(0);
            storedComment.setComment(null);
            storedComment.setDate(new Date());
            commentService.updateComment(storedComment.getId(), storedComment);
        });
    }

    @Test
    void testUpdateInvalidComment5() {
        assertThrows(IllegalOperationException.class, () -> {
            CommentEntity storedComment = commentList.get(0);
            storedComment.setComment("Updated Comment");
            storedComment.setDate(null);
            commentService.updateComment(storedComment.getId(), storedComment);
        });
    }

    @Test
    void testUpdateInvalidComment6() {
        assertThrows(IllegalOperationException.class, () -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date()); 
            calendar.add(Calendar.YEAR, 1);
            CommentEntity storedComment = commentList.get(0);
            storedComment.setComment("Updated Comment");
            storedComment.setDate(calendar.getTime());
            commentService.updateComment(storedComment.getId(), storedComment);
        });
    }

    @Test
    void testDeleteComment() throws IllegalOperationException, EntityNotFoundException {
        CommentEntity storedComment = commentList.get(0);
        commentService.deleteComment(storedComment.getId());
        CommentEntity deletedComment = entityManager.find(CommentEntity.class, storedComment.getId());
        assertNull(deletedComment);
    }

    @Test
    void testDeleteInvalidComment1() {
        assertThrows(IllegalOperationException.class, () -> {
            commentService.deleteComment(null);
        });
    }

    @Test
    void testDeleteInvalidComment2() {
        assertThrows(IllegalOperationException.class, () -> {
            commentService.deleteComment(0L);
        });
    }

    @Test
    void testDeleteInvalidComment3() {
        assertThrows(EntityNotFoundException.class, () -> {
            commentService.deleteComment(321L);
        });
    }
}
