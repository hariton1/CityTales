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
import java.util.stream.Collectors;

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
            logger.info("Fetching all users interests");
            return this.userInterestService.getAllUserInterests();
        } catch (Exception e) {
            logger.error("Error fetching users interests: {}", e.getMessage());
            throw new RuntimeException("Error fetching users interests", e);
        }
    }

    @GetMapping("/interest_id={interestId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInterestDTO> getUserInterestsByInterestId(@PathVariable("interestId") int interestId) {
        try {
            logger.info("Fetching user interests by id: {}", interestId);
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
            logger.info("Creating user interest: {}", userInterestDTO.toString());
            this.userInterestService.saveNewUserInterest(userInterestDTO);
        } catch (Exception e) {
            logger.error("Error creating user interest {}: {}", userInterestDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating user interest", e);
        }
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserInterest(@RequestBody UserInterestDTO userInterestDTO) {
        try {
            logger.info("Deleting user interest: {}", userInterestDTO.toString());
            this.userInterestService.deleteUserInterest(userInterestDTO);
        } catch (Exception e) {
            logger.error("Error deleting user interest {}: {}", userInterestDTO.toString(), e.getMessage());
            throw new RuntimeException("Error deleting user interest", e);
        }
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInterestDTO> getOwnUserInterests(@AuthenticationPrincipal Jwt jwt) {
        try {
            UUID userId = UUID.fromString(jwt.getSubject());
            logger.info("Fetching user interests by user_id: {}", jwt.getSubject());
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

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInterestDTO> getUserInterestsByUserId(@PathVariable("userId") UUID userId) {
        try {
            logger.info("Fetching user interests by user_id: {}", userId.toString());
            return this.userInterestService.getUserInterestsByUserId(userId);
        } catch (Exception e) {
            logger.error("Error fetching user interests by user_id {}: {}", userId.toString(), e.getMessage());
            throw new RuntimeException("Error fetching user interests", e);
        }
    }

    @GetMapping("/interest_ids_user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> getUserInterestIdsByUserId(@PathVariable("userId") UUID userId) {
        try {
            return this.userInterestService.getUserInterestsByUserId(userId).stream().map(UserInterestDTO::getInterestId).collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<Integer>(); //"An internal server error occurred => " + e.getMessage();
        }
    }



}
