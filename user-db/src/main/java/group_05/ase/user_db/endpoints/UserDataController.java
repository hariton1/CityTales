package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserDataDTO;
import group_05.ase.user_db.services.UserDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userData")
public class UserDataController {

    private static final Logger logger = LoggerFactory.getLogger(UserDataController.class);

    private final UserDataService userDataService;

    public UserDataController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDataDTO> getAllUserData() {
        try {
            logger.info("Fetching all user data");
            return this.userDataService.getAllUserData();
        } catch (Exception e) {
            logger.error("Error fetching user data: {}", e.getMessage());
            throw new RuntimeException("Error fetching user data", e);
        }
    }

    @GetMapping("/id={userDataId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDataDTO getUserDataById(@PathVariable("userDataId") int userDataId) {
        try {
            logger.info("Fetching user data by id: {}", userDataId);
            return this.userDataService.getUserDataById(userDataId);
        } catch (Exception e) {
            logger.error("Error fetching user data by id {}: {}", userDataId, e.getMessage());
            throw new RuntimeException("Error fetching user data", e);
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDataDTO getUserDataByUserId(@PathVariable("userId") UUID userId) {
        try {
            logger.info("Fetching user data by user_id: {}", userId.toString());
            return this.userDataService.getUserDataByUserId(userId);
        } catch (Exception e) {
            logger.error("Error fetching user data by user_id {}: {}", userId.toString(), e.getMessage());
            throw new RuntimeException("Error fetching user data", e);
        }
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDataDTO saveUserData(@RequestBody UserDataDTO userDataDTO) {
        try {
            logger.info("Saving user data: {}", userDataDTO.toString());
            return this.userDataService.saveUserData(userDataDTO);
        } catch (Exception e) {
            logger.error("Error saving user data {}: {}", userDataDTO.toString(), e.getMessage());
            throw new RuntimeException("Error saving user data", e);
        }
    }

}
