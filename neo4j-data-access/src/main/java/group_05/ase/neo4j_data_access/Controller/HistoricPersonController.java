package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.HistoricalPersonEntity;
import group_05.ase.neo4j_data_access.Service.HistoricPersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historicPerson")
public class HistoricPersonController {

    private final HistoricPersonService historicPersonService;

    public HistoricPersonController(HistoricPersonService historicPersonService) {
        this.historicPersonService = historicPersonService;
    }

    @GetMapping("/by/id/{wikiDataId}")
    public ResponseEntity<HistoricalPersonEntity> getHistoricPersonById(@PathVariable("wikiDataId") String wikiDataId) {
        HistoricalPersonEntity person = historicPersonService.getPersonById(wikiDataId);

        if (person != null) {
            return ResponseEntity.ok(person);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<HistoricalPersonEntity>> getHistoricPersonsByPartialName(@PathVariable String partialName) {
        List<HistoricalPersonEntity> people = historicPersonService.getPersonsByPartialName(partialName);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @GetMapping("/links/by/id/{wikiDataId}")
    public ResponseEntity<List<HistoricalPersonEntity>> getLinksById(@PathVariable String wikiDataId) {
        List<HistoricalPersonEntity> people = historicPersonService.getAllLinkedHistoricPersonsById(wikiDataId);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }
}
