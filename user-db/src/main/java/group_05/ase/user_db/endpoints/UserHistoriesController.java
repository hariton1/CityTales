package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserHistoryDTO;
import group_05.ase.user_db.services.UserHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userHistories")
public class UserHistoriesController {

    private final UserHistoryService userHistoryService;

    public UserHistoriesController(UserHistoryService userHistoryService) {
        this.userHistoryService = userHistoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserHistoryDTO> getAllUserHistories() {
        try {
            return this.userHistoryService.getAllUserHistories();
        } catch (Exception e) {
            return new ArrayList<UserHistoryDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/id={userHistoryId}")
    @ResponseStatus(HttpStatus.OK)
    public UserHistoryDTO getUserHistoriesById(@PathVariable("userHistoryId") int userId) {
        try {
            return this.userHistoryService.getUserHistoriesById(userId);
        } catch (Exception e) {
            return new UserHistoryDTO(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserHistoryDTO> getUserHistoriesByUserId(@PathVariable("userId") UUID userId) {
        try {
            return this.userHistoryService.getUserHistoriesByUserId(userId);
        } catch (Exception e) {
            return new ArrayList<UserHistoryDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/article_id={articleId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserHistoryDTO> getUserHistoriesByArticleId(@PathVariable("articleId") int articleId) {
        try {
            return this.userHistoryService.getUserHistoriesByArticleId(articleId);
        } catch (Exception e) {
            return new ArrayList<UserHistoryDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewUserHistory(@RequestBody UserHistoryDTO userHistoryDTO) {
        try {
            this.userHistoryService.saveNewUserHistory(userHistoryDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserHistory(@RequestBody UserHistoryDTO userHistoryDTO) {
        try {
            this.userHistoryService.saveChangedUserHistory(userHistoryDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
