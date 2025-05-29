package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserBadgeDTO;
import group_05.ase.user_db.services.UserBadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userBadges")
public class UserBadgeController {

    private final UserBadgeService userBadgeService;

    public UserBadgeController(UserBadgeService userBadgeService) {
        this.userBadgeService = userBadgeService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserBadgeDTO> getAllUserBadges() {
        try {
            return this.userBadgeService.getAllUserBadges();
        } catch (Exception e) {
            return new ArrayList<UserBadgeDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserBadgeDTO> getUserBadgesByUserId(@PathVariable("userId") UUID userId) {
        try {
            return this.userBadgeService.getUserBadgesByUserId(userId);
        } catch (Exception e) {
            return new ArrayList<UserBadgeDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/article_id={articleId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserBadgeDTO> getUserBadgesBaArticleId(@PathVariable("articleId") int articleId) {
        try {
            return this.userBadgeService.getUserBadgesByArticleId(articleId);
        } catch (Exception e) {
            return new ArrayList<UserBadgeDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserBadgeDTO createNewBadge(@RequestBody UserBadgeDTO userBadgeDTO) {
        try {
            return this.userBadgeService.saveNewBadge(userBadgeDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new UserBadgeDTO();
        }
    }

}
