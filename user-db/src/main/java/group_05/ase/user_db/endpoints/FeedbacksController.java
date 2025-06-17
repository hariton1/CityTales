package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.FeedbackDTO;
import group_05.ase.user_db.services.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/feedbacks")
public class FeedbacksController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbacksController.class);

    private final FeedbackService feedbackService;

    public FeedbacksController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FeedbackDTO> getAllFeedbacks() {
        try {
            logger.info("Fetching all feedbacks");
            return this.feedbackService.getAllFeedbacks();
        } catch (Exception e) {
            logger.error("Error fetching feedbacks: {}", e.getMessage());
            throw new RuntimeException("Error fetching feedbacks", e);
        }
    }

    @GetMapping("/id={feedbackId}")
    @ResponseStatus(HttpStatus.OK)
    public FeedbackDTO getFeedbackById(@PathVariable("feedbackId") int feedbackId) {
        try {
            logger.info("Fetching feedback by id: {}", feedbackId);
            return this.feedbackService.getFeedbackById(feedbackId);
        } catch (Exception e) {
            logger.error("Error fetching feedback by id {}: {}", feedbackId, e.getMessage());
            throw new RuntimeException("Error fetching feedback", e);
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<FeedbackDTO> getFeedbacksByUserId(@PathVariable("userId") UUID userId) {
        try {
            logger.info("Fetching feedback by user_id: {}", userId.toString());
            return this.feedbackService.getFeedbacksByUserId(userId);
        } catch (Exception e) {
            logger.error("Error fetching feedback by user_id {}: {}", userId.toString(), e.getMessage());
            throw new RuntimeException("Error fetching feedback", e);
        }
    }

    @GetMapping("/article_id={articleId}")
    @ResponseStatus(HttpStatus.OK)
    public List<FeedbackDTO> getFeedbacksByArticleId(@PathVariable("articleId") int articleId) {
        try {
            logger.info("Fetching feedback by article_id: {}", articleId);
            return this.feedbackService.getFeedbacksByArticleId(articleId);
        } catch (Exception e) {
            logger.error("Error fetching feedback by article_id {}: {}", articleId, e.getMessage());
            throw new RuntimeException("Error fetching feedback", e);
        }
    }

    @GetMapping("/content={content}")
    @ResponseStatus(HttpStatus.OK)
    public List<FeedbackDTO> getFeedbacksByFbContentLike(@PathVariable("content") String content) {
        try {
            logger.info("Fetching feedback by content: {}", content);
            return this.feedbackService.getFeedbacksByFbContentLike(content);
        } catch (Exception e) {
            logger.error("Error fetching feedback by content {}: {}", content, e.getMessage());
            throw new RuntimeException("Error fetching feedback", e);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        try {
            logger.info("Creating feedback: {}", feedbackDTO.toString());
            this.feedbackService.saveNewFeedback(feedbackDTO);
        } catch (Exception e) {
            logger.error("Error creating feedback {}: {}", feedbackDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating feedback", e);
        }
    }

    @PutMapping("/approve/id={feedbackId}")
    @ResponseStatus(HttpStatus.OK)
    public void approveFeedback(@PathVariable("feedbackId") int feedbackId) {
        try {
            logger.info("Approving feedback by id: {}", feedbackId);
            this.feedbackService.approveFeedback(feedbackId);
        } catch (Exception e) {
            logger.error("Error approving feedback by id {}: {}", feedbackId, e.getMessage());
            throw new RuntimeException("Error approving feedback", e);
        }
    }

    @DeleteMapping("/delete/id={feedbackId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFeedback(@PathVariable("feedbackId") int feedbackId) {
        try {
            logger.info("Deleting feedback by id: {}", feedbackId);
            this.feedbackService.deleteFeedback(feedbackId);
        } catch (Exception e) {
            logger.error("Error deleting feedback by id {}: {}", feedbackId, e.getMessage());
            throw new RuntimeException("Error deleting feedback", e);
        }
    }

}
