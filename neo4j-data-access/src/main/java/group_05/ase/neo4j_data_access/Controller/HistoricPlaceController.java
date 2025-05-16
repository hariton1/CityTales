package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
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
}
