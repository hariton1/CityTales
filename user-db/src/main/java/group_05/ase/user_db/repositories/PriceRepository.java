package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<PriceEntity, Integer> {

    List<PriceEntity> findByLocationIdOrderByPriceIdAsc(int locationId);

}
