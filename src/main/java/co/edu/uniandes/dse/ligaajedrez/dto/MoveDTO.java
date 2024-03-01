package co.edu.uniandes.dse.ligaajedrez.dto;

import lombok.Data;

@Data
public class MoveDTO {
    private long id;
    private String notation;
    private PlayerDTO player;
}
