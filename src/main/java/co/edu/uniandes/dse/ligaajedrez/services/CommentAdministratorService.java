package co.edu.uniandes.dse.ligaajedrez.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.AdministratorEntity;
import co.edu.uniandes.dse.ligaajedrez.entities.CommentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.repositories.AdministratorRepository;
import co.edu.uniandes.dse.ligaajedrez.repositories.CommentRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommentAdministratorService {
    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public AdministratorEntity addAdministrator(Long administratorId, Long commentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of associating the administrator with ID {0} to the comment with ID {1}.", administratorId, commentId);
        if (administratorId == null || administratorId == 0L || commentId == null || commentId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if (commentEntity.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = " + Long.toString(commentId)  + " was not found.");
        }
        commentEntity.get().setAuthor(administratorEntity.get());
        log.info("Finish the process of associating the administrator with ID {0} to the comment with ID {1}.", administratorId, commentId);
        return administratorEntity.get();
    }

    @Transactional
    public AdministratorEntity getAdministrator(Long commentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of querying the administrator of the comment with ID {0}", commentId);
        if (commentId == null || commentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if (commentEntity.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = " + Long.toString(commentId)  + " was not found.");
        }
        AdministratorEntity administratorEntity = commentEntity.get().getAuthor();
        if (administratorEntity == null) {
            throw new EntityNotFoundException("The administrator of the comment with ID = " + Long.toString(commentId)  + " was not found.");
        }
        log.info("Finish the process of querying the administrator of the comment with ID {0}", commentId);
        return administratorEntity;
    }

    @Transactional
    public AdministratorEntity replaceAdministrator(Long administratorId, Long commentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of replacing the administrator with ID {0} to the comment with ID {1}.", administratorId, commentId);
        if (administratorId == null || administratorId == 0L || commentId == null || commentId == 0L) {
            throw new IllegalOperationException("IDs cannot be null or empty.");
        }
        Optional<AdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty()) {
            throw new EntityNotFoundException("The administrator with ID = " + Long.toString(administratorId)  + " was not found.");
        }
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if (commentEntity.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = " + Long.toString(commentId)  + " was not found.");
        }
        commentEntity.get().setAuthor(administratorEntity.get());
        log.info("Finish the process of replacing the administrator with ID {0} to the comment with ID {1}.", administratorId, commentId);
        return administratorEntity.get();
    }

    @Transactional
    public void removeAdministrator(Long commentId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Start the process of disassociating the administrator of the comment with ID {0}", commentId);
        if (commentId == null || commentId == 0L) {
            throw new IllegalOperationException("The ID cannot be null or empty");
        }
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if (commentEntity.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = " + Long.toString(commentId)  + " was not found.");
        }
        AdministratorEntity administratorEntity = commentEntity.get().getAuthor();
        if (administratorEntity == null) {
            throw new EntityNotFoundException("The administrator of the comment with ID = " + Long.toString(commentId)  + " was not found.");
        }
        commentEntity.get().setAuthor(null);
        log.info("Finish the process of disassociating the administrator of the comment with ID {0}", commentId);
    }
}
