package co.edu.uniandes.dse.ligaajedrez.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class AdministratorEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String photoURL;
    @Column(unique = true, nullable = false)
    private String IDNumber;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;

    @PodamExclude
    @ManyToMany
    private List<LeagueEntity> leagues = new ArrayList<>();
}
