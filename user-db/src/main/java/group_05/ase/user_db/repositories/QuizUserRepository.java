package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.QuizUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizUserRepository extends JpaRepository<QuizUserEntity, Integer> {

    List<QuizUserEntity> findByPlayer(UUID player);

}
