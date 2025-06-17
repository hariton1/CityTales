package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group_05.ase.neo4j_data_access.Service.Interface.ISearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final ISearchService searchService;

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    public SearchController(ISearchService searchService) {
        this.searchService = searchService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/searchPersons")
    public ResponseEntity<List<ViennaHistoryWikiPersonObject>> searchHistoricPersons(@RequestParam String query) {
        List<ViennaHistoryWikiPersonObject> persons = searchService.searchPersonsWithKeyword(query);
        logger.info("Fetching historic persons for query: {}", query);
        if (persons.isEmpty()) {
            logger.info("Historic persons for query {} not found", query);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(persons);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/searchPlaces")
    public ResponseEntity<List<ViennaHistoryWikiBuildingObject>> searchHistoricPlaces(@RequestParam String query) {
        List<ViennaHistoryWikiBuildingObject> places = searchService.searchPlacesWithKeyword(query);
        logger.info("Fetching historic buildings for query: {}", query);
        if (places.isEmpty()) {
            logger.info("Historic buildings for query {} not found", query);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(places);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/searchEvents")
    public ResponseEntity<List<ViennaHistoryWikiEventObject>> searchHistoricEvents(@RequestParam String query) {
        List<ViennaHistoryWikiEventObject> events = searchService.searchEventsWithKeyword(query);
        logger.info("Fetching historic events for query: {}", query);
        if (events.isEmpty()) {
            logger.info("Historic events for query {} not found", query);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

}
