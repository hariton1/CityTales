package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserInterestDTO;
import group_05.ase.user_db.restData.UserInterestWithWeightDTO;
import group_05.ase.user_db.services.UserInterestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userInterests")
public class UserInterestsController {

    private static final Logger logger = LoggerFactory.getLogger(UserInterestsController.class);

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
            logger.error("Error fetching user interests: {}", e.getMessage());
            throw new RuntimeException("Error fetching user interests", e);
        }
    }

    @GetMapping("/interest_id={interestId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInterestDTO> getUserInterestsByInterestId(@PathVariable("interestId") int interestId) {
        try {
            return this.userInterestService.getUserInterestsByInterestId(interestId);
        } catch (Exception e) {
            logger.error("Error fetching user interests by id {}: {}", interestId, e.getMessage());
            throw new RuntimeException("Error fetching user interests", e);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewUserInterest(@RequestBody UserInterestDTO userInterestDTO) {
        try {
            this.userInterestService.saveNewUserInterest(userInterestDTO);
        } catch (Exception e) {
            logger.error("Error creating user interest by id {}: {}", userInterestDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating user interest", e);
        }
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserInterest(@RequestBody UserInterestDTO userInterestDTO) {
        try {
            this.userInterestService.deleteUserInterest(userInterestDTO);
        } catch (Exception e) {
            logger.error("Error deleting user interest by id {}: {}", userInterestDTO.toString(), e.getMessage());
            throw new RuntimeException("Error deleting user interest", e);
        }
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInterestDTO> getOwnUserInterests(@AuthenticationPrincipal Jwt jwt) {
        try {
            UUID userId = UUID.fromString(jwt.getSubject());
            return this.userInterestService.getUserInterestsByUserId(userId);
        } catch (Exception e) {
            logger.error("Error fetching user interests by user_id {}: {}", jwt.getSubject(), e.getMessage());
            throw new RuntimeException("Error fetching user interests", e);
        }
    }

    @GetMapping("/user/{userId}/interests/with-weight")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInterestWithWeightDTO> getUserInterestsWithWeightByUserId(@PathVariable("userId") UUID userId) {
        return userInterestService.getUserInterestsWithWeightByUserId(userId);
    }


}
