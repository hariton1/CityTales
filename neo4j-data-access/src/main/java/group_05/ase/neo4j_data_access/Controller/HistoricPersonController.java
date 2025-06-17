package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactExtractorService;
import group_05.ase.neo4j_data_access.Service.Implementation.HistoricBuildingService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historicPerson")
public class HistoricPersonController {

    private final IHistoricPersonService historicPersonService;
    private final FunFactExtractorService funFactExtractorService;
    private final HistoricBuildingService historicBuildingService;

    private static final Logger logger = LoggerFactory.getLogger(HistoricPersonController.class);

    public HistoricPersonController(IHistoricPersonService historicPersonService, FunFactExtractorService funFactExtractorService, HistoricBuildingService historicBuildingService) {
        this.historicPersonService = historicPersonService;
        this.funFactExtractorService = funFactExtractorService;
        this.historicBuildingService = historicBuildingService;
    }

    @GetMapping("/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<ViennaHistoryWikiPersonObject> getHistoricPersonById(@PathVariable int viennaHistoryWikiId) {
        ViennaHistoryWikiPersonObject personDTO = historicPersonService.getPersonById(viennaHistoryWikiId);
        logger.info("Fetching historic person by id: {}", viennaHistoryWikiId);
        if (personDTO != null) {
            return ResponseEntity.ok(personDTO);
        } else {
            logger.info("Historic person by id {} not found", viennaHistoryWikiId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<ViennaHistoryWikiPersonObject>> getHistoricPersonsByPartialName(@PathVariable String partialName) {
        List<ViennaHistoryWikiPersonObject> people = historicPersonService.getPersonsByPartialName(partialName);
        logger.info("Fetching historic person by name: {}", partialName);
        if (people.isEmpty()) {
            logger.info("Historic person by name {} not found", partialName);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(people);
    }

    @GetMapping("/links/persons/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiPersonObject>> getLinkedPersonsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiPersonObject> people = historicPersonService.getAllLinkedHistoricPersonsById(viennaHistoryWikiId);
        logger.info("Fetching linked historic persons for person with id {}", viennaHistoryWikiId);
        if (people.isEmpty()) {
            logger.info("Linked historic persons for person with id {} not found", viennaHistoryWikiId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @GetMapping("/links/buildings/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiBuildingObject>> getLinkedBuildingsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiBuildingObject> buildings = historicPersonService.getAllLinkedHistoricBuildingsById(viennaHistoryWikiId);
        logger.info("Fetching linked historic buildings for person with id {}", viennaHistoryWikiId);
        if (buildings.isEmpty()) {
            logger.info("Linked historic buildings for person with id {} not found", viennaHistoryWikiId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/links/events/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiEventObject>> getLinkedEventsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiEventObject> events = historicPersonService.getAllLinkedHistoricEventsById(viennaHistoryWikiId);
        logger.info("Fetching linked historic events for person with id {}", viennaHistoryWikiId);
        if (events.isEmpty()) {
            logger.info("Linked historic events for person with id {} not found", viennaHistoryWikiId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }
}

