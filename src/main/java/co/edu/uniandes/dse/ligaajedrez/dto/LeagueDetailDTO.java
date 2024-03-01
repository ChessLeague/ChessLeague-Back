package co.edu.uniandes.dse.ligaajedrez.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LeagueDetailDTO extends LeagueDTO {
    private List<PlayerDTO> players = new ArrayList<>();
    private List<TournamentDTO> tournaments = new ArrayList<>();
    private List<AdministratorDTO> administrators = new ArrayList<>();
}
