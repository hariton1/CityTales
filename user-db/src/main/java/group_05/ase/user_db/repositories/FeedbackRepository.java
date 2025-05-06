package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Integer> {

    FeedbackEntity findByFeedbackId(int feedbackId);

    List<FeedbackEntity> findByUserId(UUID userId);

    List<FeedbackEntity> findByArticleId(int articleId);

    List<FeedbackEntity> findByFbContentContaining(String content);

}
