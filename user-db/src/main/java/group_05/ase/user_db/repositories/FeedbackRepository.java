package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Integer> {

    List<FeedbackEntity> findAllByArticleId(int articleId);

}
