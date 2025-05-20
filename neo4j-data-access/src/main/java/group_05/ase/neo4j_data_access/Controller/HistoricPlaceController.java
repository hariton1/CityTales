package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricEventDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
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
    public ResponseEntity<HistoricBuildingDTO> getHistoricalPlaceById(@PathVariable int viennaHistoryWikiId) {
        HistoricBuildingDTO place = historicPlaceService.getBuildingById(viennaHistoryWikiId);

        if (place != null) {
            return ResponseEntity.ok(place);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<HistoricBuildingDTO>> getHistoricPlacesByPartialName(@PathVariable String partialName) {
        List<HistoricBuildingDTO> places = historicPlaceService.getBuildingByPartialName(partialName);
        if (places.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(places);
    }

    @GetMapping("/by/location")
    public ResponseEntity<List<HistoricBuildingDTO>> getHistoricalPlacesByLocation(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {

        List<HistoricBuildingDTO> places = historicPlaceService.findHistoricalBuildingWithinRadius(latitude, longitude, radius);
        if (places.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(places);
    }

    @GetMapping("/links/buildings/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<HistoricBuildingDTO>> getLinkedBuildingsById(@PathVariable int viennaHistoryWikiId) {
        List<HistoricBuildingDTO> buildings = historicPlaceService.getAllLinkedHistoricBuildingsById(viennaHistoryWikiId);
        if (buildings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/links/persons/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<HistoricPersonDTO>> getLinkedPersonsById(@PathVariable int viennaHistoryWikiId) {
        List<HistoricPersonDTO> persons = historicPlaceService.getAllLinkedHistoricPersonsById(viennaHistoryWikiId);
        if (persons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/links/events/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<HistoricEventDTO>> getLinkedEventsById(@PathVariable int viennaHistoryWikiId) {
        List<HistoricEventDTO> events = historicPlaceService.getAllLinkedHistoricEventsById(viennaHistoryWikiId);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }
}
