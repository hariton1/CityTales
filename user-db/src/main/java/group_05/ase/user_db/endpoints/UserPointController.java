package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserPointDTO;
import group_05.ase.user_db.services.UserPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userPoints")
public class UserPointController {

    private static final Logger logger = LoggerFactory.getLogger(UserPointController.class);

    private final UserPointService userPointService;

    public UserPointController(UserPointService userPointService) {
        this.userPointService = userPointService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserPointDTO> getAllUserPoints() {
        try {
            logger.info("Fetching all user points");
            return this.userPointService.getAllUserPoints();
        } catch (Exception e) {
            logger.error("Error fetching user points: {}", e.getMessage());
            throw new RuntimeException("Error fetching user points", e);
        }
    }

    @GetMapping("/id={userPointId}")
    @ResponseStatus(HttpStatus.OK)
    public UserPointDTO getUserPointById(@PathVariable("userPointId") int userPointId) {
        try {
            logger.info("Fetching user points by id: {}", userPointId);
            return this.userPointService.getUserPointById(userPointId);
        } catch (Exception e) {
            logger.error("Error fetching user points by id {}: {}", userPointId, e.getMessage());
            throw new RuntimeException("Error fetching user points", e);
        }
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserPointDTO> getUserPointsByUserId(@PathVariable("userId") UUID userId) {
        try {
            logger.info("Fetching user points by user_id: {}", userId.toString());
            return this.userPointService.getUserPointsByUserId(userId);
        } catch (Exception e) {
            logger.error("Error fetching user points by user_id {}: {}", userId.toString(), e.getMessage());
            throw new RuntimeException("Error fetching user points", e);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewPoints(@RequestBody UserPointDTO userPointDTO) {
        try {
            logger.info("Creating user points: {}", userPointDTO.toString());
            this.userPointService.saveNewPoints(userPointDTO);
        } catch (Exception e) {
            logger.error("Error creating user points {}: {}", userPointDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating user points", e);
        }
    }

}
