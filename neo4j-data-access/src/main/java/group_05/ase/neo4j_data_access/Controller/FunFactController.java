package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.DTO.FunFactCardDTO;
import group_05.ase.neo4j_data_access.DTO.FunFactResult;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactExtractorService;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactMapper;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricEventService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricPersonService;
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
    public ResponseEntity<FunFactCardDTO> getFunFactForBuilding(@PathVariable int id) {
        ViennaHistoryWikiBuildingObject building = buildingService.getBuildingById(id);
        if (building == null || building.getContentGerman() == null || building.getContentGerman().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        try {
            FunFactResult funFactResult = funFactExtractorService.extractFunFactWithML(building.getContentGerman());
            FunFactCardDTO card = FunFactMapper.mapToFunFactCard(building, funFactResult);
            return ResponseEntity.ok(card);
        } catch (ResourceAccessException ex) {
            // ML-Service nicht erreichbar
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }


    @GetMapping("/person/{id}")
    public ResponseEntity<FunFactCardDTO> getFunFactForPerson(@PathVariable int id) {
        ViennaHistoryWikiPersonObject person = personService.getPersonById(id);
        if (person == null || person.getContentGerman() == null || person.getContentGerman().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        FunFactResult funFactResult = funFactExtractorService.extractFunFactHybridWithReason(person.getContentGerman());
        FunFactCardDTO card = FunFactMapper.mapToFunFactCard(person, funFactResult);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<FunFactCardDTO> getFunFactForEvent(@PathVariable int id) {
        ViennaHistoryWikiEventObject event = eventService.getEventById(id);
        if (event == null || event.getContentGerman() == null || event.getContentGerman().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        FunFactResult funFactResult = funFactExtractorService.extractFunFactHybridWithReason(event.getContentGerman());
        FunFactCardDTO card = FunFactMapper.mapToFunFactCard(event, funFactResult);
        return ResponseEntity.ok(card);
    }
}
