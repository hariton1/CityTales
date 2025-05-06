package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.FeedbackDTO;
import group_05.ase.user_db.services.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/feedbacks")
public class FeedbacksController {

    private final FeedbackService feedbackService;

    public FeedbacksController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /* TODO write tests */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FeedbackDTO> getAllFeedbacks() {
        try {
            return this.feedbackService.getAllFeedbacks();
        } catch (Exception e) {
            return new ArrayList<FeedbackDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/id={feedbackId}")
    @ResponseStatus(HttpStatus.OK)
    public FeedbackDTO getFeedbackById(@PathVariable("feedbackId") int feedbackId) {
        try {
            return this.feedbackService.getFeedbackById(feedbackId);
        } catch (Exception e) {
            return new FeedbackDTO(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<FeedbackDTO> getFeedbacksByUserId(@PathVariable("userId") UUID userId) {
        try {
            return this.feedbackService.getFeedbacksByUserId(userId);
        } catch (Exception e) {
            return new ArrayList<FeedbackDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/article_id={articleId}")
    @ResponseStatus(HttpStatus.OK)
    public List<FeedbackDTO> getFeedbacksByArticleId(@PathVariable("articleId") int articleId) {
        try {
            return this.feedbackService.getFeedbacksByArticleId(articleId);
        } catch (Exception e) {
            return new ArrayList<FeedbackDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/content={content}")
    @ResponseStatus(HttpStatus.OK)
    public List<FeedbackDTO> getFeedbacksByFbContentLike(@PathVariable("content") String content) {
        try {
            return this.feedbackService.getFeedbacksByFbContentLike(content);
        } catch (Exception e) {
            return new ArrayList<FeedbackDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        try {
            this.feedbackService.saveNewFeedback(feedbackDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
