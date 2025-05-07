package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserInterestDTO;
import group_05.ase.user_db.services.UserInterestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userInterests")
public class UserInterestsController {

    private final UserInterestService userInterestService;

    public UserInterestsController(UserInterestService userInterestService) {
        this.userInterestService = userInterestService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserInterestDTO> getAllUserInterests() {
        try {
            return this.userInterestService.getAllUserInterests();
        } catch (Exception e) {
            return new ArrayList<UserInterestDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInterestDTO> getUserInterestsByUserId(@PathVariable("userId") UUID userId) {
        try {
            return this.userInterestService.getUserInterestsByUserId(userId);
        } catch (Exception e) {
            return new ArrayList<UserInterestDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/interest_id={interestId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInterestDTO> getUserInterestsByInterestId(@PathVariable("interestId") int interestId) {
        try {
            return this.userInterestService.getUserInterestsByInterestId(interestId);
        } catch (Exception e) {
            return new ArrayList<UserInterestDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewUserInterest(@RequestBody UserInterestDTO userInterestDTO) {
        try {
            this.userInterestService.saveNewUserInterest(userInterestDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserInterest(@RequestBody UserInterestDTO userInterestDTO) {
        try {
            this.userInterestService.deleteUserInterest(userInterestDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
