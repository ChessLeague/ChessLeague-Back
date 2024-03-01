package co.edu.uniandes.dse.ligaajedrez.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class OpeningEntity extends BaseEntity {
    private String name;
    @Column(nullable = false)
    private OpeningType classification;

    @PodamExclude
    @OneToMany(fetch = FetchType.LAZY)
    private List<MoveEntity> moves = new ArrayList<>();
}
