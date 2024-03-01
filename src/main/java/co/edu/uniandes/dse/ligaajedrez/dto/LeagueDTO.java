package co.edu.uniandes.dse.ligaajedrez.dto;

import lombok.Data;

@Data
public class LeagueDTO {
    private long id;
    private String name;
    private String city;
    private String address;
    private String phone;
    private String webURL;
}
