package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.AuthUserEntity;
import group_05.ase.user_db.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<AuthUserEntity, UUID>  {

}
