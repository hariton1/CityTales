package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.HistoricalPersonEntity;
import group_05.ase.neo4j_data_access.Entity.HistoricalPlaceEntity;
import group_05.ase.neo4j_data_access.Service.Neo4jAccessService;
import org.springframework.data.neo4j.types.GeographicPoint2d;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DataAccessController {

    private final Neo4jAccessService neo4jAccessService;

    public DataAccessController(Neo4jAccessService neo4jAccessService) {
        this.neo4jAccessService = neo4jAccessService;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/by/location")
    public List<HistoricalPlaceEntity> getHistoricalPlacesByLocation(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {

        return neo4jAccessService.findHistoricalPlacesWithinRadius(latitude, longitude, radius);
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<HistoricalPersonEntity>> getHistoricPersonsByPartialName(@PathVariable String partialName) {
        List<HistoricalPersonEntity> people = neo4jAccessService.getPersonsByPartialName(partialName);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @GetMapping("/by/id/{wikiDataId}")
    public ResponseEntity<HistoricalPersonEntity> getHistoricPersonById(@PathVariable("wikiDataId") String wikiDataId) {
        HistoricalPersonEntity person = neo4jAccessService.getPersonById(wikiDataId);

        if (person != null) {
            return ResponseEntity.ok(person);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/links/by/id/{wikiDataId}")
    public ResponseEntity<List<HistoricalPersonEntity>> getLinksById(@PathVariable String wikiDataId) {
        List<HistoricalPersonEntity> people = neo4jAccessService.getAllLinkedHistoricPersonsById(wikiDataId);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @GetMapping("/places/by/id/{wikiDataId}")
    public ResponseEntity<HistoricalPlaceEntity> getHistoricalPlaceById(@PathVariable String wikiDataId) {
        HistoricalPlaceEntity place = neo4jAccessService.getPlaceById(wikiDataId);

        if (place != null) {
            return ResponseEntity.ok(place);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
