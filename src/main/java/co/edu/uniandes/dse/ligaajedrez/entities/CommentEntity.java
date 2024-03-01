package co.edu.uniandes.dse.ligaajedrez.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class CommentEntity extends BaseEntity {
    @Column(nullable = false)
    private String comment;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @PodamExclude
    @OneToOne
    private AdministratorEntity author;
}
