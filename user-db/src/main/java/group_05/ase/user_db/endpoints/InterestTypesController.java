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
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public List<InterestTypeDTO> getAllInterestTypes() {
        try {
            return null;//this.interestTypeService.getAllInterestTypes();
        } catch (Exception e) {
            return new ArrayList<InterestTypeDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/id={interestTypeId}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public InterestTypeDTO getInterestTypesByInterestTypeId(@PathVariable("interestTypeId") int interestTypeId) {
        try {
            return null;//this.interestTypeService.getInterestById(interestTypeId);
        } catch (Exception e) {
            return new InterestTypeDTO();//"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/name={typeName}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public InterestTypeDTO getInterestTypesByTypeName(@PathVariable("typeName") String typeName) {
        try {
            return null;//this.interestTypeService.getInterestByName(typeName);
        } catch (Exception e) {
            return new InterestTypeDTO();//"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/description={description}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public List<InterestTypeDTO> getInterestTypesByDescriptionLike(@PathVariable("description") String description) {
        try {
            return null;//this.interestTypeService.getInterestTypesByDescriptionLike(description);
        } catch (Exception e) {
            return new ArrayList<InterestTypeDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void createNewInterestType(@RequestBody InterestTypeDTO interestTypeDTO) {
        try {
            System.out.println("not supported");
            //this.interestTypeService.saveNewInterestType(interestTypeDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
