package group_05.ase.user_db;

import group_05.ase.user_db.entities.FeedbackEntity;
import group_05.ase.user_db.repositories.FeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserDbApplicationTests {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private FeedbackRepository feedbackRepository;

	@Test
	public void testGetFeedbackById() {
		FeedbackEntity testFeedback = new FeedbackEntity(1, UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572a"),100,"Completely correct");
		entityManager.persist(testFeedback);
		entityManager.flush();

		FeedbackEntity feedbackEntity = feedbackRepository.findByFeedbackId(1);

		assertThat(feedbackEntity.getFbContent()).isEqualTo(testFeedback.getFbContent());
	}

}
