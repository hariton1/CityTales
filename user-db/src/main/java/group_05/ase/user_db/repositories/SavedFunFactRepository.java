package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.SavedFunFactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavedFunFactRepository extends JpaRepository<SavedFunFactEntity, Integer> {

    List<SavedFunFactEntity> findAllByUserId(UUID userId);

}
