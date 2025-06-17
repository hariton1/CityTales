package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historicEvent")
public class HistoricEventController {

    private final IHistoricEventService historicEventService;

    private static final Logger logger = LoggerFactory.getLogger(HistoricEventController.class);

    public HistoricEventController(IHistoricEventService historicEventService) {
        this.historicEventService = historicEventService;
    }

    @GetMapping("/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<ViennaHistoryWikiEventObject> getHistoricEventById(@PathVariable int viennaHistoryWikiId) {
        ViennaHistoryWikiEventObject eventDTO = historicEventService.getEventById(viennaHistoryWikiId);
        logger.info("Fetching historic event by id: {}", viennaHistoryWikiId);
        if (eventDTO != null) {
            return ResponseEntity.ok(eventDTO);
        } else {
            logger.info("Historic event by id {} not found", viennaHistoryWikiId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<ViennaHistoryWikiEventObject>> getHistoricEventByPartialName(@PathVariable String partialName) {
        List<ViennaHistoryWikiEventObject> events = historicEventService.getEventByPartialName(partialName);
        logger.info("Fetching historic event by name: {}", partialName);
        if (events.isEmpty()) {
            logger.info("Historic event by name {} not found", partialName);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(events);
    }

    @GetMapping("/links/events/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiEventObject>> getLinkedEventsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiEventObject> events = historicEventService.getAllLinkedHistoricEventsById(viennaHistoryWikiId);
        logger.info("Fetching linked historic events for event with id {}", viennaHistoryWikiId);
        if (events.isEmpty()) {
            logger.info("Linked historic events for event with id {} not found", viennaHistoryWikiId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/links/buildings/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiBuildingObject>> getLinkedBuildingsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiBuildingObject> buildings = historicEventService.getAllLinkedHistoricBuildingsById(viennaHistoryWikiId);
        logger.info("Fetching linked historic buildings for event with id {}", viennaHistoryWikiId);
        if (buildings.isEmpty()) {
            logger.info("Linked historic buildings for event with id {} not found", viennaHistoryWikiId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/links/persons/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiPersonObject>> getLinkedPersonsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiPersonObject> persons = historicEventService.getAllLinkedHistoricPersonsById(viennaHistoryWikiId);
        logger.info("Fetching linked historic persons for event with id {}", viennaHistoryWikiId);
        if (persons.isEmpty()) {
            logger.info("Linked historic persons for event with id {} not found", viennaHistoryWikiId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(persons);
    }
}
