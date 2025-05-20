package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricEventDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricPersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historicPerson")
public class HistoricPersonController {

    private final IHistoricPersonService historicPersonService;

    public HistoricPersonController(IHistoricPersonService historicPersonService ) {
        this.historicPersonService = historicPersonService;
    }

    @GetMapping("/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<HistoricPersonDTO> getHistoricPersonById(@PathVariable int viennaHistoryWikiId) {
        HistoricPersonDTO personDTO = historicPersonService.getPersonById(viennaHistoryWikiId);

        if (personDTO != null) {
            return ResponseEntity.ok(personDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<HistoricPersonDTO>> getHistoricPersonsByPartialName(@PathVariable String partialName) {
        List<HistoricPersonDTO> people = historicPersonService.getPersonsByPartialName(partialName);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(people);
    }

    @GetMapping("/links/persons/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<HistoricPersonDTO>> getLinkedPersonsById(@PathVariable int viennaHistoryWikiId) {
        List<HistoricPersonDTO> people = historicPersonService.getAllLinkedHistoricPersonsById(viennaHistoryWikiId);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @GetMapping("/links/buildings/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<HistoricBuildingDTO>> getLinkedBuildingsById(@PathVariable int viennaHistoryWikiId) {
        List<HistoricBuildingDTO> buildings = historicPersonService.getAllLinkedHistoricBuildingsById(viennaHistoryWikiId);
        if (buildings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/links/events/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<HistoricEventDTO>> getLinkedEventsById(@PathVariable int viennaHistoryWikiId) {
        List<HistoricEventDTO> events = historicPersonService.getAllLinkedHistoricEventsById(viennaHistoryWikiId);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }
}
