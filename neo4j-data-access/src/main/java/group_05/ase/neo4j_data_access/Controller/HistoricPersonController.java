package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
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
    public ResponseEntity<ViennaHistoryWikiPersonObject> getHistoricPersonById(@PathVariable int viennaHistoryWikiId) {
        ViennaHistoryWikiPersonObject personDTO = historicPersonService.getPersonById(viennaHistoryWikiId);

        if (personDTO != null) {
            return ResponseEntity.ok(personDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<ViennaHistoryWikiPersonObject>> getHistoricPersonsByPartialName(@PathVariable String partialName) {
        List<ViennaHistoryWikiPersonObject> people = historicPersonService.getPersonsByPartialName(partialName);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(people);
    }

    @GetMapping("/links/persons/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiPersonObject>> getLinkedPersonsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiPersonObject> people = historicPersonService.getAllLinkedHistoricPersonsById(viennaHistoryWikiId);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @GetMapping("/links/buildings/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiBuildingObject>> getLinkedBuildingsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiBuildingObject> buildings = historicPersonService.getAllLinkedHistoricBuildingsById(viennaHistoryWikiId);
        if (buildings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/links/events/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<ViennaHistoryWikiEventObject>> getLinkedEventsById(@PathVariable int viennaHistoryWikiId) {
        List<ViennaHistoryWikiEventObject> events = historicPersonService.getAllLinkedHistoricEventsById(viennaHistoryWikiId);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }
}
