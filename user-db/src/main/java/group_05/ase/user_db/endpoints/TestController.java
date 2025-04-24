package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    private final InterestService interestService;
    private final InterestTypeService interestTypeService;
    private final FeedbackService feedbackService;
    private final UserInterestService userInterestService;
    private final UserHistoryService userHistoryService;

    @Autowired
    public TestController(InterestService interestService, InterestTypeService interestTypeService, FeedbackService feedbackService,
                          UserInterestService userInterestService, UserHistoryService userHistoryService) {
        this.interestService = interestService;
        this.interestTypeService = interestTypeService;
        this.feedbackService = feedbackService;
        this.userInterestService = userInterestService;
        this.userHistoryService = userHistoryService;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Connection to PostgreDB is working!";
    }

    @GetMapping("/interests")
    @ResponseStatus(HttpStatus.OK)
    public String getAllInterests() {
        try {
            return this.interestService.getAllInterests().toString();
        } catch (Exception e) {
            return "An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/interestTypes")
    @ResponseStatus(HttpStatus.OK)
    public String getAllInterestTypes() {
        try {
            return this.interestTypeService.getAllInterestTypes().toString();
        } catch (Exception e) {
            return "An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/feedbacks")
    @ResponseStatus(HttpStatus.OK)
    public String getAllFeedbacks() {
        try {
            return this.feedbackService.getAllFeedbacks().toString();
        } catch (Exception e) {
            return "An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/userInterests")
    @ResponseStatus(HttpStatus.OK)
    public String getAllUserInterests() {
        try {
            return this.userInterestService.getAllUserInterests().toString();
        } catch (Exception e) {
            return "An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/userHistories")
    @ResponseStatus(HttpStatus.OK)
    public String getAllUserHistories() {
        try {
            return this.userHistoryService.getAllUserHistories().toString();
        } catch (Exception e) {
            return "An internal server error occurred => " + e.getMessage();
        }
    }

}
