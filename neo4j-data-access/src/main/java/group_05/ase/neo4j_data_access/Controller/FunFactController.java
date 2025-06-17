package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.DTO.FunFactResult;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactExtractorService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricEventService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

@RestController
@RequestMapping("/api/funfact")
public class FunFactController {

    private final IHistoricBuildingService buildingService;
    private final IHistoricPersonService personService;
    private final IHistoricEventService eventService;
    private final FunFactExtractorService funFactExtractorService;

    private static final Logger logger = LoggerFactory.getLogger(FunFactController.class);

    public FunFactController(IHistoricBuildingService buildingService,
                             IHistoricPersonService personService,
                             IHistoricEventService eventService,
                             FunFactExtractorService funFactExtractorService) {
        this.buildingService = buildingService;
        this.personService = personService;
        this.eventService = eventService;
        this.funFactExtractorService = funFactExtractorService;
    }

    @GetMapping("/building/{id}")
    public ResponseEntity<FunFactResult> getFunFactForBuilding(@PathVariable int id) {
        ViennaHistoryWikiBuildingObject building = buildingService.getBuildingById(id);
        logger.info("Fetching fun fact for building with id: {}", id);
        if (building == null || building.getContentGerman() == null || building.getContentGerman().isBlank()) {
            logger.info("Fun fact for building with id {} not found.", id);
            return ResponseEntity.notFound().build();
        }
        try {
            FunFactResult funFactResult = funFactExtractorService.extractFunFactWithML(building.getContentGerman());
            return ResponseEntity.ok(funFactResult);
        } catch (ResourceAccessException ex) {
            logger.error("Error fetching fun fact for building with id: {}", id);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }


    @GetMapping("/person/{id}")
    public ResponseEntity<FunFactResult> getFunFactForPerson(@PathVariable int id) {
        ViennaHistoryWikiPersonObject person = personService.getPersonById(id);
        logger.info("Fetching fun fact for person with id: {}", id);
        if (person == null || person.getContentGerman() == null || person.getContentGerman().isBlank()) {
            logger.info("Fun fact for person with id {} not found.", id);
            return ResponseEntity.notFound().build();
        }
        try {
            FunFactResult funFactResult = funFactExtractorService.extractFunFactWithML(person.getContentGerman());
         return ResponseEntity.ok(funFactResult);
        } catch (ResourceAccessException ex) {
            logger.error("Error fetching fun fact for person with id: {}", id);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<FunFactResult> getFunFactForEvent(@PathVariable int id) {
        ViennaHistoryWikiEventObject event = eventService.getEventById(id);
        logger.info("Fetching fun fact for event with id: {}", id);
        if (event == null || event.getContentGerman() == null || event.getContentGerman().isBlank()) {
            logger.info("Fun fact for event with id {} not found.", id);
            return ResponseEntity.notFound().build();
        }
        try {
        FunFactResult funFactResult = funFactExtractorService.extractFunFactHybridWithReason(event.getContentGerman());
        return ResponseEntity.ok(funFactResult);
        } catch (ResourceAccessException ex) {
            logger.error("Error fetching fun fact for event with id: {}", id);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
