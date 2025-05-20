package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.FeedbackEntity;
import group_05.ase.user_db.repositories.FeedbackRepository;
import group_05.ase.user_db.restData.FeedbackDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceTest {

    @Mock
    FeedbackRepository feedbackRepository;

    @InjectMocks
    FeedbackService feedbackService;

    private final FeedbackEntity feedbackEntity = new FeedbackEntity (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            1,
            100.0,
            "Completely correct",
            null
    );

    private final ArrayList<FeedbackEntity> feedbackEntities = new ArrayList<>(List.of(feedbackEntity));

    @Test
    public void testGetAllFeedbacks() {

        when(feedbackRepository.findAll()).thenReturn(feedbackEntities);

        ArrayList<FeedbackDTO> feedbackDTOs = new ArrayList<>(feedbackService.getAllFeedbacks());

        assertThat(feedbackDTOs.getFirst().getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());
        assertThat(feedbackDTOs.getFirst().getUserId()).isEqualTo(feedbackEntity.getUserId());
        assertThat(feedbackDTOs.getFirst().getArticleId()).isEqualTo(feedbackEntity.getArticleId());
        assertThat(feedbackDTOs.getFirst().getRating()).isEqualTo(feedbackEntity.getRating());
        assertThat(feedbackDTOs.getFirst().getFbContent()).isEqualTo(feedbackEntity.getFbContent());

        System.out.println("Test testGetAllFeedbacks passed!");

    }

    @Test
    public void testGetFeedbackById() {

        when(feedbackRepository.findByFeedbackId(any(int.class))).thenReturn(feedbackEntity);

        FeedbackDTO feedbackDTO = feedbackService.getFeedbackById(feedbackEntity.getFeedbackId());

        assertThat(feedbackDTO.getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());

        System.out.println("Test testGetFeedbackById passed!");

    }

    @Test
    public void testGetFeedbacksByUserId() {

        when(feedbackRepository.findByUserId(any(UUID.class))).thenReturn(feedbackEntities);

        ArrayList<FeedbackDTO> feedbackDTOs = new ArrayList<>(feedbackService.getFeedbacksByUserId(feedbackEntity.getUserId()));

        assertThat(feedbackDTOs.getFirst().getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());
        assertThat(feedbackDTOs.getFirst().getUserId()).isEqualTo(feedbackEntity.getUserId());
        assertThat(feedbackDTOs.getFirst().getArticleId()).isEqualTo(feedbackEntity.getArticleId());
        assertThat(feedbackDTOs.getFirst().getRating()).isEqualTo(feedbackEntity.getRating());
        assertThat(feedbackDTOs.getFirst().getFbContent()).isEqualTo(feedbackEntity.getFbContent());

        System.out.println("Test testGetFeedbacksByUserId passed!");

    }

    @Test
    public void testGetFeedbacksByArticleId() {

        when(feedbackRepository.findByArticleId(any(int.class))).thenReturn(feedbackEntities);

        ArrayList<FeedbackDTO> feedbackDTOs = new ArrayList<>(feedbackService.getFeedbacksByArticleId(feedbackEntity.getArticleId()));

        assertThat(feedbackDTOs.getFirst().getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());
        assertThat(feedbackDTOs.getFirst().getUserId()).isEqualTo(feedbackEntity.getUserId());
        assertThat(feedbackDTOs.getFirst().getArticleId()).isEqualTo(feedbackEntity.getArticleId());
        assertThat(feedbackDTOs.getFirst().getRating()).isEqualTo(feedbackEntity.getRating());
        assertThat(feedbackDTOs.getFirst().getFbContent()).isEqualTo(feedbackEntity.getFbContent());

        System.out.println("Test testGetFeedbacksByArticleId passed!");

    }

    @Test
    public void testGetFeedbacksByFbContentLike() {

        when(feedbackRepository.findByFbContentContaining(any(String.class))).thenReturn(feedbackEntities);

        ArrayList<FeedbackDTO> feedbackDTOs = new ArrayList<>(feedbackService.getFeedbacksByFbContentLike(feedbackEntity.getFbContent()));

        assertThat(feedbackDTOs.getFirst().getFeedbackId()).isEqualTo(feedbackEntity.getFeedbackId());
        assertThat(feedbackDTOs.getFirst().getUserId()).isEqualTo(feedbackEntity.getUserId());
        assertThat(feedbackDTOs.getFirst().getArticleId()).isEqualTo(feedbackEntity.getArticleId());
        assertThat(feedbackDTOs.getFirst().getRating()).isEqualTo(feedbackEntity.getRating());
        assertThat(feedbackDTOs.getFirst().getFbContent()).isEqualTo(feedbackEntity.getFbContent());

        System.out.println("Test testGetFeedbacksByFbContentLike passed!");

    }

    @Test
    public void testSaveNewFeedback() {

        System.out.println("Test testSaveNewFeedback not provided.");

    }

}
