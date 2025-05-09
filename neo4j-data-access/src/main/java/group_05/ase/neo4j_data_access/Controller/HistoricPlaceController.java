package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.HistoricalPlaceEntity;
import group_05.ase.neo4j_data_access.Service.HistoricPlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historicPlace")
public class HistoricPlaceController {

    private final HistoricPlaceService historicPlaceService;

    public  HistoricPlaceController(HistoricPlaceService historicPlaceService) {
        this.historicPlaceService = historicPlaceService;
    }

    @GetMapping("/places/by/id/{wikiDataId}")
    public ResponseEntity<HistoricalPlaceEntity> getHistoricalPlaceById(@PathVariable String wikiDataId) {
        HistoricalPlaceEntity place = historicPlaceService.getPlaceById(wikiDataId);

        if (place != null) {
            return ResponseEntity.ok(place);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/location")
    public List<HistoricalPlaceEntity> getHistoricalPlacesByLocation(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {

        return historicPlaceService.findHistoricalPlacesWithinRadius(latitude, longitude, radius);
    }
}
