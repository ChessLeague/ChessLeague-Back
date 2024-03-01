package co.edu.uniandes.dse.ligaajedrez.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class PlayerEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String photoURL;
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    private String birthPlace;
    private Integer eloRating;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;

    @PodamExclude
    @ManyToMany
    private List<LeagueEntity> leagues = new ArrayList<>();

    @PodamExclude
    @ManyToMany
    private List<TournamentEntity> tournaments = new ArrayList<>();

    @PodamExclude
    @ManyToMany
    private List<GameEntity> games = new ArrayList<>();
}
