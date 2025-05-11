package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserPointDTO;
import group_05.ase.user_db.services.UserPointService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userPoints")
public class UserPointController {

    private final UserPointService userPointService;

    public UserPointController(UserPointService userPointService) {
        this.userPointService = userPointService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserPointDTO> getAllUserPoints() {
        try {
            return this.userPointService.getAllUserPoints();
        } catch (Exception e) {
            return new ArrayList<UserPointDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/id={userPointId}")
    @ResponseStatus(HttpStatus.OK)
    public UserPointDTO getUserPointById(@PathVariable("userPointId") int userPointId) {
        try {
            return this.userPointService.getUserPointById(userPointId);
        } catch (Exception e) {
            return new UserPointDTO(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserPointDTO> getUserPointsByUserId(@PathVariable("userId") UUID userId) {
        try {
            return this.userPointService.getUserPointsByUserId(userId);
        } catch (Exception e) {
            return new ArrayList<UserPointDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewPoints(@RequestBody UserPointDTO userPointDTO) {
        try {
            this.userPointService.saveNewPoints(userPointDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
