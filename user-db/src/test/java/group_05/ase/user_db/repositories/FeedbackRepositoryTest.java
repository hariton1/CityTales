package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.FeedbackEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest()
public class FeedbackRepositoryTest {

    @Autowired
    FeedbackRepository feedbackRepository;

    private final FeedbackEntity feedbackEntity = new FeedbackEntity(
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572a"),
            100.0,
            "Completely correct",
            null);

    @Test
    public void testFindByFeedbackId() {

        assertThat(feedbackRepository.findByFeedbackId(1)).isNotNull();
        assertThat(feedbackRepository.findByFeedbackId(1).getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());
        assertThat(feedbackRepository.findByFeedbackId(1).getUserId()).isEqualTo(feedbackEntity.getUserId());
        assertThat(feedbackRepository.findByFeedbackId(1).getArticleId()).isEqualTo(feedbackEntity.getArticleId());
        assertThat(feedbackRepository.findByFeedbackId(1).getRating()).isEqualTo(feedbackEntity.getRating());
        assertThat(feedbackRepository.findByFeedbackId(1).getFbContent()).isEqualTo(feedbackEntity.getFbContent());

    }

}
