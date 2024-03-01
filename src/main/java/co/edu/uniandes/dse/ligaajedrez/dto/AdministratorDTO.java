package co.edu.uniandes.dse.ligaajedrez.dto;

import lombok.Data;

@Data
public class AdministratorDTO {
    private long id;
    private String name;
    private String photoURL;
    private String IDNumber;
    private String username;
    private String password;
}
