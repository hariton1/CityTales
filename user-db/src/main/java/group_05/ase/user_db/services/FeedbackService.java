package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.FeedbackEntity;
import group_05.ase.user_db.repositories.FeedbackRepository;
import group_05.ase.user_db.restData.FeedbackDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FeedbackService {

    private final FeedbackRepository repository;

    public FeedbackService(FeedbackRepository repository) {
        this.repository = repository;
    }

    public List<FeedbackDTO> getAllFeedbacks() {

        ArrayList<FeedbackDTO> feedbacks = new ArrayList<>();
        List<FeedbackEntity> tmp = this.repository.findAll();

        for(FeedbackEntity feedback : tmp) {
            feedbacks.add(new FeedbackDTO(feedback.getFeedbackId(), feedback.getUserId(), feedback.getArticleId(),
                                          feedback.getRating(), feedback.getFbContent(), feedback.getCreDat()));
        }

        return feedbacks;

    }

    public FeedbackDTO getFeedbackById(int feedbackId) {

        FeedbackEntity tmp = this.repository.findByFeedbackId(feedbackId);

        return new FeedbackDTO(tmp.getFeedbackId(), tmp.getUserId(), tmp.getArticleId(),
                tmp.getRating(), tmp.getFbContent(), tmp.getCreDat());

    }

    public List<FeedbackDTO> getFeedbacksByUserId(UUID userId) {

        ArrayList<FeedbackDTO> feedbacks = new ArrayList<>();
        List<FeedbackEntity> tmp = this.repository.findByUserId(userId);

        for(FeedbackEntity feedback : tmp) {
            feedbacks.add(new FeedbackDTO(feedback.getFeedbackId(), feedback.getUserId(), feedback.getArticleId(),
                    feedback.getRating(), feedback.getFbContent(), feedback.getCreDat()));
        }

        return feedbacks;

    }

    public List<FeedbackDTO> getFeedbacksByArticleId(int articleId) {

        ArrayList<FeedbackDTO> feedbacks = new ArrayList<>();
        List<FeedbackEntity> tmp = this.repository.findByArticleId(articleId);

        for(FeedbackEntity feedback : tmp) {
            feedbacks.add(new FeedbackDTO(feedback.getFeedbackId(), feedback.getUserId(), feedback.getArticleId(),
                    feedback.getRating(), feedback.getFbContent(), feedback.getCreDat()));
        }

        return feedbacks;

    }

    public List<FeedbackDTO> getFeedbacksByFbContentLike(String content) {

        ArrayList<FeedbackDTO> feedbacks = new ArrayList<>();
        List<FeedbackEntity> tmp = this.repository.findByFbContentContaining(content);

        for(FeedbackEntity feedback : tmp) {
            feedbacks.add(new FeedbackDTO(feedback.getFeedbackId(), feedback.getUserId(), feedback.getArticleId(),
                    feedback.getRating(), feedback.getFbContent(), feedback.getCreDat()));
        }

        return feedbacks;

    }

    public void saveNewFeedback(FeedbackDTO feedbackDTO) {

        FeedbackEntity tmp = new FeedbackEntity();

        tmp.setUserId(feedbackDTO.getUserId());
        tmp.setArticleId(feedbackDTO.getArticleId());
        tmp.setRating(feedbackDTO.getRating());
        tmp.setFbContent(feedbackDTO.getFbContent());
        tmp.setCreDat(feedbackDTO.getCreDat());

        this.repository.save(tmp);

    }

    public void approveFeedback(int feedbackId) {
        FeedbackEntity tmp = this.repository.findByFeedbackId(feedbackId);

        tmp.setApproved("Y");

        this.repository.save(tmp);
    }

    public void deleteFeedback(int feedbackId) {
        this.repository.deleteById(feedbackId);
    }

}
