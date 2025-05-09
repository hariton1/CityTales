package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.FeedbackEntity;
import group_05.ase.user_db.repositories.FeedbackRepository;
import group_05.ase.user_db.restData.FeedbackDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceTest {

    @Mock
    FeedbackRepository feedbackRepository;

    @InjectMocks
    FeedbackService feedbackService;

    @Test
    public void testGetFeedbackById() {

        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.setFeedbackId(1);

        when(feedbackRepository.findByFeedbackId(any(int.class))).thenReturn(feedbackEntity);

        FeedbackDTO feedbackDTO = feedbackService.getFeedbackById(feedbackEntity.getFeedbackId());

        assertThat(feedbackDTO.getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());

        System.out.println(feedbackEntity.toString());
        System.out.println(feedbackDTO.toString());

    }

}
