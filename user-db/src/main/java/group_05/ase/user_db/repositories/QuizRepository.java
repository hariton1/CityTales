package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<QuizEntity, Integer> {

    QuizEntity findById(int id);

    List<QuizEntity> findByCreator(UUID creator);

}
