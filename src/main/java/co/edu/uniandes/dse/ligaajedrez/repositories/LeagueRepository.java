package co.edu.uniandes.dse.ligaajedrez.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.ligaajedrez.entities.LeagueEntity;

@Repository
public interface LeagueRepository extends JpaRepository<LeagueEntity, Long> {
    
}
