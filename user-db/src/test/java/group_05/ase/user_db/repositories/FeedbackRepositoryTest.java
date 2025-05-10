package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.FeedbackEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
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
            null
    );

    @Test
    public void testFindByFeedbackId() {

        FeedbackEntity tmp = feedbackRepository.findByFeedbackId(1);

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());
        assertThat(tmp.getUserId()).isEqualTo(feedbackEntity.getUserId());
        assertThat(tmp.getArticleId()).isEqualTo(feedbackEntity.getArticleId());
        assertThat(tmp.getRating()).isEqualTo(feedbackEntity.getRating());
        assertThat(tmp.getFbContent()).isEqualTo(feedbackEntity.getFbContent());

        System.out.println("Test testFindByFeedbackId passed!");

    }

    @Test
    public void testFindByUserId() {

        ArrayList<FeedbackEntity> tmp = new ArrayList<>(feedbackRepository.findByUserId(UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b")));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());
        assertThat(tmp.getFirst().getUserId()).isEqualTo(feedbackEntity.getUserId());
        assertThat(tmp.getFirst().getArticleId()).isEqualTo(feedbackEntity.getArticleId());
        assertThat(tmp.getFirst().getRating()).isEqualTo(feedbackEntity.getRating());
        assertThat(tmp.getFirst().getFbContent()).isEqualTo(feedbackEntity.getFbContent());

        System.out.println("Test testFindByUserId passed!");

    }

    @Test
    public void testFindByArticleId() {

        ArrayList<FeedbackEntity> tmp = new ArrayList<>(feedbackRepository.findByArticleId(UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572a")));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());
        assertThat(tmp.getFirst().getUserId()).isEqualTo(feedbackEntity.getUserId());
        assertThat(tmp.getFirst().getArticleId()).isEqualTo(feedbackEntity.getArticleId());
        assertThat(tmp.getFirst().getRating()).isEqualTo(feedbackEntity.getRating());
        assertThat(tmp.getFirst().getFbContent()).isEqualTo(feedbackEntity.getFbContent());

        System.out.println("Test testFindByArticleId passed!");

    }

    @Test
    public void testFindByFbContentContaining() {

        ArrayList<FeedbackEntity> tmp = new ArrayList<>(feedbackRepository.findByFbContentContaining("corr"));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());
        assertThat(tmp.getFirst().getUserId()).isEqualTo(feedbackEntity.getUserId());
        assertThat(tmp.getFirst().getArticleId()).isEqualTo(feedbackEntity.getArticleId());
        assertThat(tmp.getFirst().getRating()).isEqualTo(feedbackEntity.getRating());
        assertThat(tmp.getFirst().getFbContent()).isEqualTo(feedbackEntity.getFbContent());

        System.out.println("Test testFindByFbContentContaining passed!");

    }

}
