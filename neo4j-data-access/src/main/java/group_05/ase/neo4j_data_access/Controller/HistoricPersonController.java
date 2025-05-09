package group_05.ase.neo4j_data_access.Controller;

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

    @GetMapping("/by/id/{wikiDataId}")
    public ResponseEntity<HistoricPersonDTO> getHistoricPersonById(@PathVariable("wikiDataId") String wikiDataId) {
        HistoricPersonDTO personDTO = historicPersonService.getPersonById(wikiDataId);

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

    @GetMapping("/links/by/id/{wikiDataId}")
    public ResponseEntity<List<HistoricPersonDTO>> getLinksById(@PathVariable String wikiDataId) {
        List<HistoricPersonDTO> people = historicPersonService.getAllLinkedHistoricPersonsById(wikiDataId);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }
}
