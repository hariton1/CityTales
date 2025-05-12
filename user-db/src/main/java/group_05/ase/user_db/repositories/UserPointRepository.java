package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserPointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserPointRepository extends JpaRepository<UserPointEntity, Integer> {

    UserPointEntity findByUserPointId(Integer userPointId);

    List<UserPointEntity> findByUserId(UUID userId);

}
