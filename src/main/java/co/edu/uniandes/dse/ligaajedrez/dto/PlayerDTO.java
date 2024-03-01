package co.edu.uniandes.dse.ligaajedrez.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PlayerDTO {
    private long id;
    private String name;
    private String photoURL;
    private Date birthDate;
    private String birthPlace;
    private Integer eloRating;
    private String username;
    private String password;
}
