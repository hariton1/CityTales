package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserHistoryDTO;
import group_05.ase.user_db.services.UserHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userHistories")
public class UserHistoriesController {

    private static final Logger logger = LoggerFactory.getLogger(UserHistoriesController.class);

    private final UserHistoryService userHistoryService;

    public UserHistoriesController(UserHistoryService userHistoryService) {
        this.userHistoryService = userHistoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserHistoryDTO> getAllUserHistories() {
        try {
            logger.info("Fetching all user histories");
            return this.userHistoryService.getAllUserHistories();
        } catch (Exception e) {
            logger.error("Error fetching user histories: {}", e.getMessage());
            throw new RuntimeException("Error fetching user histories", e);
        }
    }

    @GetMapping("/id={userHistoryId}")
    @ResponseStatus(HttpStatus.OK)
    public UserHistoryDTO getUserHistoriesById(@PathVariable("userHistoryId") int userId) {
        try {
            logger.info("Fetching user histories by id: {}", userId);
            return this.userHistoryService.getUserHistoriesById(userId);
        } catch (Exception e) {
            logger.error("Error fetching user histories by id {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching user histories", e);
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserHistoryDTO> getUserHistoriesByUserId(@PathVariable("userId") UUID userId) {
        try {
            logger.info("Fetching user histories by user_id: {}", userId.toString());
            return this.userHistoryService.getUserHistoriesByUserId(userId);
        } catch (Exception e) {
            logger.error("Error fetching user histories by user_id {}: {}", userId.toString(), e.getMessage());
            throw new RuntimeException("Error fetching user histories", e);
        }
    }

    @GetMapping("/article_id={articleId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserHistoryDTO> getUserHistoriesByArticleId(@PathVariable("articleId") int articleId) {
        try {
            logger.info("Fetching user histories by article_id: {}", articleId);
            return this.userHistoryService.getUserHistoriesByArticleId(articleId);
        } catch (Exception e) {
            logger.error("Error fetching user histories by article_id {}: {}", articleId, e.getMessage());
            throw new RuntimeException("Error fetching user histories", e);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserHistoryDTO createNewUserHistory(@RequestBody UserHistoryDTO userHistoryDTO) {
        try {
            logger.info("Creating user history: {}", userHistoryDTO.toString());
            return this.userHistoryService.saveNewUserHistory(userHistoryDTO);
        } catch (Exception e) {
            logger.error("Error creating user history {}: {}", userHistoryDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating user history", e);
        }
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserHistory(@RequestBody UserHistoryDTO userHistoryDTO) {
        try {
            logger.info("Updating user history: {}", userHistoryDTO.toString());
            this.userHistoryService.saveChangedUserHistory(userHistoryDTO);
        } catch (Exception e) {
            logger.error("Error updating user history {}: {}", userHistoryDTO.toString(), e.getMessage());
            throw new RuntimeException("Error updating user history", e);
        }
    }

}
