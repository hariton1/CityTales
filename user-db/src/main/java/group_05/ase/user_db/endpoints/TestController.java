package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.*;
import group_05.ase.user_db.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


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
    @ResponseStatus(HttpStatus.OK)
    public String testEndpoint() {
        return "Connection to PostgreDB is working!";
    }

    @GetMapping("/interests")
    @ResponseStatus(HttpStatus.OK)
    public List<InterestDTO> getAllInterests() {
        try {
            return this.interestService.getAllInterests();
        } catch (Exception e) {
            return new ArrayList<InterestDTO>();//"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/interestTypes")
    @ResponseStatus(HttpStatus.OK)
    public List<InterestTypeDTO> getAllInterestTypes() {
        try {
            return this.interestTypeService.getAllInterestTypes();
        } catch (Exception e) {
            return new ArrayList<InterestTypeDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    /* TODO write tests */
    @GetMapping("/feedbacks")
    @ResponseStatus(HttpStatus.OK)
    public List<FeedbackDTO> getAllFeedbacks() {
        try {
            return this.feedbackService.getAllFeedbacks();
        } catch (Exception e) {
            return new ArrayList<FeedbackDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/userInterests")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInterestDTO> getAllUserInterests() {
        try {
            return this.userInterestService.getAllUserInterests();
        } catch (Exception e) {
            return new ArrayList<UserInterestDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/userHistories")
    @ResponseStatus(HttpStatus.OK)
    public List<UserHistoryDTO> getAllUserHistories() {
        try {
            return this.userHistoryService.getAllUserHistories();
        } catch (Exception e) {
            return new ArrayList<UserHistoryDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

}
