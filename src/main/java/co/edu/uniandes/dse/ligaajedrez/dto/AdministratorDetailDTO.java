package co.edu.uniandes.dse.ligaajedrez.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AdministratorDetailDTO extends AdministratorDTO {
    private List<LeagueDTO> leagues = new ArrayList<>();
}
