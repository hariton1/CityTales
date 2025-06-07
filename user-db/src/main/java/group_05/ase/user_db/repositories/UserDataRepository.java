package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserDataRepository extends JpaRepository<UserDataEntity, Integer> {

    UserDataEntity findByUserDataId(int userDataId);

    UserDataEntity findByUserId(UUID userId);

}
