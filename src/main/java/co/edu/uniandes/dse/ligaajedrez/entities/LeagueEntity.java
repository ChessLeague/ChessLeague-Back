package co.edu.uniandes.dse.ligaajedrez.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class LeagueEntity extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;
    private String city;
    private String address;
    private String phone;
    private String webURL;

    @PodamExclude
    @ManyToMany(mappedBy = "leagues", fetch = FetchType.LAZY)
    private List<AdministratorEntity> administrators = new ArrayList<>();

    @PodamExclude
    @ManyToMany(mappedBy = "leagues", fetch = FetchType.LAZY)
    private List<PlayerEntity> players = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "league", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentEntity> tournaments = new ArrayList<>();
}
