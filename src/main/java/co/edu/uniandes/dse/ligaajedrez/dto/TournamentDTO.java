package co.edu.uniandes.dse.ligaajedrez.dto;

import java.util.Date;

import lombok.Data;

@Data
public class TournamentDTO {
    private long id;
    private String name;
    private String location;
    private Date date;
    private String prize;
    private String image;
    private LeagueDTO league;
}
