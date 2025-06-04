package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, Integer> {

    TourEntity findByName(String name);
    List<TourEntity> findAllByUserId(String userId);
    TourEntity findAllById(Integer tourId);

}
