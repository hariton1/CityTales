package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserInterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterestEntity, Integer> {

    List<UserInterestEntity> findAllByInterestId(int interestId);

}
