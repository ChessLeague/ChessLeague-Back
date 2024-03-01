package co.edu.uniandes.dse.ligaajedrez.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TournamentDetailDTO extends TournamentDTO {
    private List<PlayerDTO> players = new ArrayList<>();
    private List<GameDTO> games = new ArrayList<>();
}
