package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.CommentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.CommentRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    @Transactional
    public CommentEntity createComment(CommentEntity commentEntity) throws IllegalOperationException {
        log.info("Start the comment creation process.");
        if (commentEntity.getId() == null || commentEntity.getId() == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Calendar calendar = Calendar.getInstance();
        if (commentEntity.getDate() == null || commentEntity.getDate().compareTo(calendar.getTime()) > 0) {
            throw new IllegalOperationException("The date cannot be null or after the current date.");
        }
        try {
            log.info("Finish the comment creation process.");
            return commentRepository.save(commentEntity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public List<CommentEntity> getComments() {
        log.info("Start the process of querying all comments.");
        return commentRepository.findAll();
    }

    @Transactional
    public CommentEntity getComment(Long commentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the comment with ID = {0}.", commentId);
        if (commentId == null || commentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if (commentEntity.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = " + commentId + " was not found.");
        }
        log.info("Finish the process of querying the comment with ID = {0}.", commentId);
        return commentEntity.get();
    }

    @Transactional
    public CommentEntity updateComment(Long commentId, CommentEntity comment) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of updating the comment with ID = {0}.", commentId);
        if (commentId == null || commentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if (commentEntity.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = " + commentId + " was not found.");
        }
        Calendar calendar = Calendar.getInstance();
        if (comment.getDate() == null || comment.getDate().compareTo(calendar.getTime()) > 0) {
            throw new IllegalOperationException("The date cannot be null or after the current date.");
        }
        comment.setId(commentId);
        try {
            log.info("Finish the process of updating the comment with ID = {0}.", commentId);
            return commentRepository.saveAndFlush(comment);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalOperationException("Some values cannot be null or repeated.");
        }
    }

    @Transactional
    public void deleteComment(Long commentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of deleting the comment with ID = {0}.", commentId);
        if (commentId == null || commentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty.");
        }
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if (commentEntity.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = " + commentId + " was not found.");
        }
        commentRepository.deleteById(commentId);
        log.info("Finish the process of deleting the comment with ID = {0}.", commentId);
    }
}
