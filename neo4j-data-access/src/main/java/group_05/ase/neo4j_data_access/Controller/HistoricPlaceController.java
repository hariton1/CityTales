package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historicPlace")
public class HistoricPlaceController {

    private final IHistoricBuildingService historicPlaceService;

    public  HistoricPlaceController(IHistoricBuildingService historicPlaceService) {
        this.historicPlaceService = historicPlaceService;
    }

    @GetMapping("/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<ViennaHistoryWikiBuildingObject> getHistoricalPlaceById(@PathVariable int viennaHistoryWikiId) {

        ViennaHistoryWikiBuildingObject place = historicPlaceService.getBuildingById(viennaHistoryWikiId);

        if (place != null) {
            return ResponseEntity.ok(place);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<ViennaHistoryWikiBuildingObject>> getHistoricPlacesByPartialName(@PathVariable String partialName) {
        List<ViennaHistoryWikiBuildingObject> places = historicPlaceService.getBuildingByPartialName(partialName);
        if (places.isEmpty()) {
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
        if (places.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(places);
    }

    @GetMapping("/links/buildings/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiBuildingObject>> getLinkedBuildingsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiBuildingObject> buildings = historicPlaceService.getAllLinkedHistoricBuildingsById(viennaHistoryWikiId);
        if (buildings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/links/persons/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiPersonObject>> getLinkedPersonsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiPersonObject> persons = historicPlaceService.getAllLinkedHistoricPersonsById(viennaHistoryWikiId);
        if (persons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/links/events/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiEventObject>> getLinkedEventsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiEventObject> events = historicPlaceService.getAllLinkedHistoricEventsById(viennaHistoryWikiId);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }
}
