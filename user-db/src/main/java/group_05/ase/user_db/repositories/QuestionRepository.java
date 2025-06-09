package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {

    List<QuestionEntity> findByQuiz(int quiz);

}