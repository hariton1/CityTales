package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.QuestionResultsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionResultsRepository extends JpaRepository<QuestionResultsEntity, Integer> {

    List<QuestionResultsEntity> findByQuestion(int question);

}
