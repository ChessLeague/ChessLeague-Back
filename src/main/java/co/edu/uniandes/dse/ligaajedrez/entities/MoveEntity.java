package co.edu.uniandes.dse.ligaajedrez.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class MoveEntity extends BaseEntity {
    @Column(nullable = false)
    private String notation;

    @PodamExclude
    @OneToOne
    private PlayerEntity player;
}
