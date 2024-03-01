package co.edu.uniandes.dse.ligaajedrez.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class TournamentEntity extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;
    private String location;
    @Temporal(TemporalType.DATE)
    private Date date;
    private String prize;
    private String image;

    @PodamExclude
    @ManyToOne
    private LeagueEntity league;

    @PodamExclude
    @ManyToMany(mappedBy = "tournaments", fetch = FetchType.LAZY)
    private List<PlayerEntity> players = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "tournament", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameEntity> games = new ArrayList<>();
}
