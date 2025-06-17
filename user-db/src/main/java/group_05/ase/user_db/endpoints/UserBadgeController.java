package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserBadgeDTO;
import group_05.ase.user_db.services.UserBadgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userBadges")
public class UserBadgeController {

    private static final Logger logger = LoggerFactory.getLogger(UserBadgeController.class);

    private final UserBadgeService userBadgeService;

    public UserBadgeController(UserBadgeService userBadgeService) {
        this.userBadgeService = userBadgeService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserBadgeDTO> getAllUserBadges() {
        try {
            logger.info("Fetching all badges");
            return this.userBadgeService.getAllUserBadges();
        } catch (Exception e) {
            logger.error("Error fetching badges: {}", e.getMessage());
            throw new RuntimeException("Error fetching badges", e);
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserBadgeDTO> getAllUserBadges(@PathVariable("userId") UUID userId) {
        try {
            logger.info("Fetching badges by user_id: {}", userId.toString());
            return this.userBadgeService.getUserBadgesByUserId(userId);
        } catch (Exception e) {
            logger.error("Error fetching badges by user_id {}: {}", userId.toString(), e.getMessage());
            throw new RuntimeException("Error fetching badges", e);
        }
    }

    @GetMapping("/article_id={article_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserBadgeDTO> getAllUserBadges(@PathVariable("article_id") int article_id) {
        try {
            logger.info("Fetching badges by article_id: {}", article_id);
            return this.userBadgeService.getUserBadgesByArticleId(article_id);
        } catch (Exception e) {
            logger.error("Error fetching badges by article_id {}: {}", article_id, e.getMessage());
            throw new RuntimeException("Error fetching badges", e);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserBadgeDTO createNewUserHistory(@RequestBody UserBadgeDTO userBadgeDTO) {
        try {
            logger.info("Creating badge: {}", userBadgeDTO.toString());
            return this.userBadgeService.saveNewBadge(userBadgeDTO);
        } catch (Exception e) {
            logger.error("Error creating badge {}: {}", userBadgeDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating badge", e);
        }
    }

}
