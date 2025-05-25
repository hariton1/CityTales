package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserInterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterestEntity, Integer> {

    List<UserInterestEntity> findByUserIdOrderByInterestIdAsc(UUID userId);

    List<UserInterestEntity> findByInterestIdOrderByInterestIdAsc(int interestId);

    UserInterestEntity findByUserIdAndInterestId(UUID userId, Integer interestId);

}
