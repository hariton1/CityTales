package group_05.ase.neo4j_data_access.Controller;


import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPlaceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group_05.ase.neo4j_data_access.Service.Interface.ISearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:7687")
public class SearchController {

    private final ISearchService searchService;

    public SearchController(ISearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/searchPersons")
    public ResponseEntity<List<HistoricPersonDTO>> searchHistoricPersons(@RequestParam String query) {
        List<HistoricPersonDTO> persons = searchService.searchPersonsWithKeyword(query);
        if (persons.isEmpty()) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/searchPlaces")
    public ResponseEntity<List<HistoricPlaceDTO>> searchHistoricPlaces(@RequestParam String query) {
        List<HistoricPlaceDTO> places = searchService.searchPlacesWithKeyword(query);
        if (places.isEmpty()) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(places);
    }

}
