package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.services.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    private final InterestService interestService;

    @Autowired
    public TestController(InterestService interestService) {
        this.interestService = interestService;
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

}
