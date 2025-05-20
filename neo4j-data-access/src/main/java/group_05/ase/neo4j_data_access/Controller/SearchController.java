package group_05.ase.neo4j_data_access.Controller;


import group_05.ase.neo4j_data_access.DTO.HistoricEventDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group_05.ase.neo4j_data_access.Service.Interface.ISearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final ISearchService searchService;

    public SearchController(ISearchService searchService) {
        this.searchService = searchService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/searchPersons")
    public ResponseEntity<List<HistoricPersonDTO>> searchHistoricPersons(@RequestParam String query) {
        List<HistoricPersonDTO> persons = searchService.searchPersonsWithKeyword(query);
        System.out.println(persons.toString());
        if (persons.isEmpty()) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(persons);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/searchPlaces")
    public ResponseEntity<List<HistoricBuildingDTO>> searchHistoricPlaces(@RequestParam String query) {
        List<HistoricBuildingDTO> places = searchService.searchPlacesWithKeyword(query);
        if (places.isEmpty()) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(places);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/searchEvents")
    public ResponseEntity<List<HistoricEventDTO>> searchHistoricEvents(@RequestParam String query) {
        List<HistoricEventDTO> events = searchService.searchEventsWithKeyword(query);
        if (events.isEmpty()) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(events);
    }

}
