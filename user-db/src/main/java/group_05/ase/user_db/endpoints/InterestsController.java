package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.InterestDTO;
import group_05.ase.user_db.services.InterestService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/interests")
public class InterestsController {

    private final InterestService interestService;

    public InterestsController(InterestService interestService) {
        this.interestService = interestService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InterestDTO> getAllInterests() {
        try {
            return this.interestService.getAllInterests();
        } catch (Exception e) {
            return new ArrayList<InterestDTO>();//"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/id={interestId}")
    @ResponseStatus(HttpStatus.OK)
    public InterestDTO getInterestByInterestId(@PathVariable("interestId") int interestId) {
        try {
            return this.interestService.getInterestById(interestId);
        } catch (Exception e) {
            return new InterestDTO();//"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/type_id={interestTypeId}")
    @ResponseStatus(HttpStatus.OK)
    public InterestDTO getInterestByInterestTypeId(@PathVariable("interestTypeId") int interestTypeId) {
        try {
            return this.interestService.getInterestByInterestTypeId(interestTypeId);
        } catch (Exception e) {
            return new InterestDTO();//"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/name={interestName}")
    @ResponseStatus(HttpStatus.OK)
    public InterestDTO getInterestByName(@PathVariable("interestName") String interestName) {
        try {
            return this.interestService.getInterestByName(interestName);
        } catch (Exception e) {
            return new InterestDTO();//"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/description={description}")
    @ResponseStatus(HttpStatus.OK)
    public List<InterestDTO> getInterestsByDescriptionLike(@PathVariable("description") String description) {
        try {
            return this.interestService.getInterestsByDescriptionLike(description);
        } catch (Exception e) {
            return new ArrayList<InterestDTO>();//"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewInterest(@RequestBody InterestDTO interestDTO) {
        try {
            this.interestService.saveNewInterest(interestDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
