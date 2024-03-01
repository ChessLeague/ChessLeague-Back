package co.edu.uniandes.dse.ligaajedrez.dto;

import java.util.Date;

import lombok.Data;

@Data
public class GameDTO {
    private long id;
    private String result;
    private Date date;
    private String image;
    private TournamentDTO tournament;
    private OpeningDTO opening;
}
