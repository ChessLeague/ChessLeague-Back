package co.edu.uniandes.dse.ligaajedrez.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CommentDTO {
    private long id;
    private String comment;
    private Date date;
    private AdministratorDTO author;
}
