package co.edu.uniandes.dse.ligaajedrez.dto;

import lombok.Data;

@Data
public class AdministratorDTO {
    private long id;
    private String name;
    private String photoURL;
    private String idNumber;
    private String username;
    private String password;
}
