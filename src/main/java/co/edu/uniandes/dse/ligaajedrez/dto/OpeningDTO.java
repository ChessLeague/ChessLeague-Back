package co.edu.uniandes.dse.ligaajedrez.dto;

import co.edu.uniandes.dse.ligaajedrez.entities.OpeningType;
import lombok.Data;

@Data
public class OpeningDTO {
    private long id;
    private String name;
    private OpeningType classification;
}
