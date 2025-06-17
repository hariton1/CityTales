package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historicPlace")
public class HistoricPlaceController {

    private final IHistoricBuildingService historicPlaceService;

    private static final Logger logger = LoggerFactory.getLogger(HistoricPlaceController.class);

    public  HistoricPlaceController(IHistoricBuildingService historicPlaceService) {
        this.historicPlaceService = historicPlaceService;
    }

    @GetMapping("/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<ViennaHistoryWikiBuildingObject> getHistoricalPlaceById(@PathVariable int viennaHistoryWikiId) {
        ViennaHistoryWikiBuildingObject place = historicPlaceService.getBuildingById(viennaHistoryWikiId);
        logger.info("Fetching historic building by id: {}", viennaHistoryWikiId);
        if (place != null) {
            return ResponseEntity.ok(place);
        } else {
            logger.info("Historic building by id {} not found", viennaHistoryWikiId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<ViennaHistoryWikiBuildingObject>> getHistoricPlacesByPartialName(@PathVariable String partialName) {
        List<ViennaHistoryWikiBuildingObject> places = historicPlaceService.getBuildingByPartialName(partialName);
        logger.info("Fetching historic building by name: {}", partialName);
        if (places.isEmpty()) {
            logger.info("Historic building by name {} not found", partialName);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(places);
    }

    @GetMapping("/by/location")
    public ResponseEntity<List<ViennaHistoryWikiBuildingObject>> getHistoricalPlacesByLocation(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {

        List<ViennaHistoryWikiBuildingObject> places = historicPlaceService.findHistoricalBuildingWithinRadius(latitude, longitude, radius);
        logger.info("Fetching historic buildings for coordinates {}, {} and radius {}", latitude, longitude, radius);
        if (places.isEmpty()) {
            logger.info("Historic buildings for coordinates {}, {} and radius {} not found", latitude, longitude, radius);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(places);
    }

    @GetMapping("/links/buildings/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiBuildingObject>> getLinkedBuildingsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiBuildingObject> buildings = historicPlaceService.getAllLinkedHistoricBuildingsById(viennaHistoryWikiId);
        logger.info("Fetching linked historic buildings for building with id {}", viennaHistoryWikiId);
        if (buildings.isEmpty()) {
            logger.info("Linked historic buildings for building with id {} not found", viennaHistoryWikiId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/links/persons/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiPersonObject>> getLinkedPersonsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiPersonObject> persons = historicPlaceService.getAllLinkedHistoricPersonsById(viennaHistoryWikiId);
        logger.info("Fetching linked historic persons for building with id {}", viennaHistoryWikiId);
        if (persons.isEmpty()) {
            logger.info("Linked historic persons for building with id {} not found", viennaHistoryWikiId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/links/events/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiEventObject>> getLinkedEventsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiEventObject> events = historicPlaceService.getAllLinkedHistoricEventsById(viennaHistoryWikiId);
        logger.info("Fetching linked historic events for building with id {}", viennaHistoryWikiId);
        if (events.isEmpty()) {
            logger.info("Linked historic events for building with id {} not found", viennaHistoryWikiId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }
}
