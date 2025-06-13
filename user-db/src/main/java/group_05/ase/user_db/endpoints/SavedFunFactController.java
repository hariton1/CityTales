package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.SavedFunFactDTO;
import group_05.ase.user_db.services.SavedFunFactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/funFacts")
public class SavedFunFactController {

    private static final Logger logger = LoggerFactory.getLogger(SavedFunFactController.class);

    private final SavedFunFactService savedFunFactService;

    public SavedFunFactController(SavedFunFactService savedFunFactService) {
        this.savedFunFactService = savedFunFactService;
    }

    @GetMapping("/user_id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<SavedFunFactDTO> getAllByUserId(@PathVariable("userId") UUID userId) {
        try {
            return this.savedFunFactService.getAllByUserId(userId);
        } catch (Exception e) {
            logger.error("Error fetching saved fun facts for user with id {}: {}", userId.toString(), e.getMessage());
            throw new RuntimeException("Error fetching saved fun facts", e);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public SavedFunFactDTO createNewSavedFunFact(@RequestBody SavedFunFactDTO savedFunFactDTO) {
        try {
            return this.savedFunFactService.saveNewSavedFunFact(savedFunFactDTO);
        } catch (Exception e) {
            logger.error("Error when creating saved fun fact {}: {}", savedFunFactDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating saved fun fact", e);
        }
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSavedFunFact(@RequestBody SavedFunFactDTO savedFunFactDTO) {
        try {
            this.savedFunFactService.deleteSavedFunFact(savedFunFactDTO);
        } catch (Exception e) {
            logger.error("Error deleting saved fun fact {}: {}", savedFunFactDTO.toString(), e.getMessage());
            throw new RuntimeException("Error deleting saved fun fact", e);
        }
    }

}
