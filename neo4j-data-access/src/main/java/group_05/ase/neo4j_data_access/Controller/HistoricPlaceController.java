package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.DTO.HistoricPlaceDTO;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricPlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historicPlace")
public class HistoricPlaceController {

    private final IHistoricPlaceService historicPlaceService;

    public  HistoricPlaceController(IHistoricPlaceService historicPlaceService) {
        this.historicPlaceService = historicPlaceService;
    }

    @GetMapping("/by/id/{wikiDataId}")
    public ResponseEntity<HistoricPlaceDTO> getHistoricalPlaceById(@PathVariable String wikiDataId) {
        HistoricPlaceDTO place = historicPlaceService.getPlaceById(wikiDataId);

        if (place != null) {
            return ResponseEntity.ok(place);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<HistoricPlaceDTO>> getHistoricPlacesByPartialName(@PathVariable String partialName) {
        List<HistoricPlaceDTO> places = historicPlaceService.getPlaceByPartialName(partialName);
        if (places.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(places);
    }

    @GetMapping("/by/location")
    public ResponseEntity<List<HistoricPlaceDTO>> getHistoricalPlacesByLocation(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {

        List<HistoricPlaceDTO> places = historicPlaceService.findHistoricalPlacesWithinRadius(latitude, longitude, radius);
        if (places.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(places);
    }
}
