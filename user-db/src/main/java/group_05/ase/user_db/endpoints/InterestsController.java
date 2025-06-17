package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.InterestDTO;
import group_05.ase.user_db.services.InterestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interests")
public class InterestsController {

    private static final Logger logger = LoggerFactory.getLogger(InterestsController.class);

    private final InterestService interestService;

    public InterestsController(InterestService interestService) {
        this.interestService = interestService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InterestDTO> getAllInterests() {
        try {
            logger.info("Fetching all interests");
            return this.interestService.getAllInterests();
        } catch (Exception e) {
            logger.error("Error fetching interests: {}", e.getMessage());
            throw new RuntimeException("Error fetching interests", e);
        }
    }

    @GetMapping("/id={interestId}")
    @ResponseStatus(HttpStatus.OK)
    public InterestDTO getInterestByInterestId(@PathVariable("interestId") int interestId) {
        try {
            logger.info("Fetching interests by id: {}", interestId);
            return this.interestService.getInterestById(interestId);
        } catch (Exception e) {
            logger.error("Error fetching interests by id {}: {}", interestId, e.getMessage());
            throw new RuntimeException("Error fetching interests", e);
        }
    }

    @GetMapping("/name={interestNameEn}")
    @ResponseStatus(HttpStatus.OK)
    public InterestDTO getInterestByNameEn(@PathVariable("interestNameEn") String interestNameEn) {
        try {
            logger.info("Fetching interests by interest_name_en: {}", interestNameEn);
            return this.interestService.getInterestByNameEn(interestNameEn);
        } catch (Exception e) {
            logger.error("Error fetching interests by interest_name_en {}: {}", interestNameEn, e.getMessage());
            throw new RuntimeException("Error fetching interests", e);
        }
    }

    @GetMapping("/de/name={interestNameDe}")
    @ResponseStatus(HttpStatus.OK)
    public InterestDTO getInterestByNameDe(@PathVariable("interestNameDe") String interestNameDe) {
        try {
            logger.info("Fetching interests by interest_name_de: {}", interestNameDe);
            return this.interestService.getInterestByNameDe(interestNameDe);
        } catch (Exception e) {
            logger.error("Error fetching interests by interest_name_de {}: {}", interestNameDe, e.getMessage());
            throw new RuntimeException("Error fetching interests", e);
        }
    }

    @GetMapping("/description={description}")
    @ResponseStatus(HttpStatus.OK)
    public List<InterestDTO> getInterestsByDescriptionLike(@PathVariable("description") String description) {
        try {
            logger.info("Fetching interests by description: {}", description);
            return this.interestService.getInterestsByDescriptionLike(description);
        } catch (Exception e) {
            logger.error("Error fetching interests by description {}: {}", description, e.getMessage());
            throw new RuntimeException("Error fetching interests", e);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewInterest(@RequestBody InterestDTO interestDTO) {
        try {
            logger.info("Creating interest: {}", interestDTO.toString());
            this.interestService.saveNewInterest(interestDTO);
        } catch (Exception e) {
            logger.error("Error creating interest {}: {}", interestDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating interest", e);
        }
    }

}
