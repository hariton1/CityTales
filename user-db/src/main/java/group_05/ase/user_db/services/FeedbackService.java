package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.FeedbackEntity;
import group_05.ase.user_db.repositories.FeedbackRepository;
import group_05.ase.user_db.restData.FeedbackDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    FeedbackRepository repository;

    public List<FeedbackDTO> getAllFeedbacks() {

        ArrayList<FeedbackDTO> feedbacks = new ArrayList<>();
        List<FeedbackEntity> tmp = this.repository.findAll();

        for(FeedbackEntity feedback : tmp) {
            feedbacks.add(new FeedbackDTO(feedback.getFeedbackId(), feedback.getUserId(), feedback.getArticleId(),
                                          feedback.getRating(), feedback.getFbContent(), feedback.getCreDat()));
        }

        return feedbacks;

    }

}
