package co.edu.uniandes.dse.ligaajedrez.services;

import co.edu.uniandes.dse.ligaajedrez.entities.CommentEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.CommentRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GameCommentService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public CommentEntity addComment(Long gameId, Long commentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the comment with ID {0} to the game with ID {1}.", commentId, gameId);
        if (gameId == null || gameId == 0L || commentId == null || commentId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if (commentEntity.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = " + commentId + " was not found.");
        }
        gameEntity.get().getComments().add(commentEntity.get());
        log.info("Finish the process of associating the comment with ID {0} to the game with ID {1}.", commentId, gameId);
        return commentEntity.get();
    }

    @Transactional
    public List<CommentEntity> getComments(Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying all comments of the game with ID {0}.", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        log.info("Finish the process of querying all comments of the game with ID {0}.", gameId);
        return gameEntity.get().getComments();
    }

    @Transactional
    public CommentEntity getComment(Long commentId, Long gameId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the comment with ID {0} in the game with ID {1}.", commentId, gameId);
        if (commentId == null || commentId == 0L || gameId == null || gameId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if (commentEntity.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = " + commentId + " was not found.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        if (!gameEntity.get().getComments().contains(commentEntity.get())) {
            throw new IllegalOperationException("The comment is not associated with the game.");
        }
        log.info("Finish the process of querying the comment with ID {0} in the game with ID {1}.", commentId, gameId);
        return commentEntity.get();
    }

    @Transactional
    public List<CommentEntity> replaceComments(Long gameId, List<CommentEntity> comments) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the comments associated with the game with ID = {0}.", gameId);
        if (gameId == null || gameId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        for (CommentEntity comment : comments) {
            Optional<CommentEntity> commentEntity = commentRepository.findById(comment.getId());
            if (commentEntity.isEmpty()) {
                throw new EntityNotFoundException("The comment with ID = " + comment.getId() + " was not found.");
            }
        }
        gameEntity.get().setComments(comments);
        log.info("Finish the process of replacing the comments associated with the game with ID = {0}.", gameId);
        return gameEntity.get().getComments();
    }

    @Transactional
    public void removeComment(Long gameId, Long commentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the comment with ID {0} from the game with ID {1}.", commentId, gameId);
        if (gameId == null || gameId == 0L || commentId == null || commentId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<GameEntity> gameEntity = gameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new EntityNotFoundException("The game with ID = " + gameId + " was not found.");
        }
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if (commentEntity.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = " + commentId + " was not found.");
        }
        if (!gameEntity.get().getComments().contains(commentEntity.get())) {
            throw new IllegalOperationException("The comment is not associated with the game.");
        }
        gameEntity.get().getComments().remove(commentEntity.get());
        log.info("Finish the process of disassociating the comment with ID {0} from the game with ID {1}.", commentId, gameId);
    }
}
