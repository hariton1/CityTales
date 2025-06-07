package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserDataDTO;
import group_05.ase.user_db.services.UserDataService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userData")
public class UserDataController {

    private final UserDataService userDataService;

    public UserDataController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDataDTO> getAllUserData() {
        try {
            return this.userDataService.getAllUserData();
        } catch (Exception e) {
            return new ArrayList<UserDataDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/id={userDataId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDataDTO getUserDataById(@PathVariable("userDataId") int userDataId) {
        try {
            return this.userDataService.getUserDataById(userDataId);
        } catch (Exception e) {
            return new UserDataDTO(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDataDTO getUserDataByUserId(@PathVariable("userId") UUID userId) {
        try {
            return this.userDataService.getUserDataByUserId(userId);
        } catch (Exception e) {
            return new UserDataDTO(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDataDTO saveUserData(@RequestBody UserDataDTO userDataDTO) {
        try {
            return this.userDataService.saveUserData(userDataDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new UserDataDTO();
        }
    }

}
