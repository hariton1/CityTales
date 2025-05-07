package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.InterestTypeDTO;
import group_05.ase.user_db.services.InterestTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/interestTypes")
public class InterestTypesController {

    private final InterestTypeService interestTypeService;

    public InterestTypesController(InterestTypeService interestTypeService) {
        this.interestTypeService = interestTypeService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InterestTypeDTO> getAllInterestTypes() {
        try {
            return this.interestTypeService.getAllInterestTypes();
        } catch (Exception e) {
            return new ArrayList<InterestTypeDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/id={interestTypeId}")
    @ResponseStatus(HttpStatus.OK)
    public InterestTypeDTO getInterestTypesByInterestTypeId(@PathVariable("interestTypeId") int interestTypeId) {
        try {
            return this.interestTypeService.getInterestById(interestTypeId);
        } catch (Exception e) {
            return new InterestTypeDTO();//"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/name={typeName}")
    @ResponseStatus(HttpStatus.OK)
    public InterestTypeDTO getInterestTypesByTypeName(@PathVariable("typeName") String typeName) {
        try {
            return this.interestTypeService.getInterestByName(typeName);
        } catch (Exception e) {
            return new InterestTypeDTO();//"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/description={description}")
    @ResponseStatus(HttpStatus.OK)
    public List<InterestTypeDTO> getInterestTypesByDescriptionLike(@PathVariable("description") String description) {
        try {
            return this.interestTypeService.getInterestTypesByDescriptionLike(description);
        } catch (Exception e) {
            return new ArrayList<InterestTypeDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewInterestType(@RequestBody InterestTypeDTO interestTypeDTO) {
        try {
            this.interestTypeService.saveNewInterestType(interestTypeDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
