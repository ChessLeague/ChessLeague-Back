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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class GameEntity extends BaseEntity {
    @Column(nullable = false)
    private String result;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;
    private String image;

    @PodamExclude
    @ManyToOne
    private TournamentEntity tournament;

    @PodamExclude
    @ManyToMany(mappedBy = "games", fetch = FetchType.LAZY)
    private List<PlayerEntity> players = new ArrayList<>();

    @PodamExclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MoveEntity> moves = new ArrayList<>();

    @PodamExclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();
    
    @PodamExclude
    @OneToOne
    private OpeningEntity opening;
}
