package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricEventDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historicEvent")
public class HistoricEventController {

    private final IHistoricEventService historicEventService;

    public HistoricEventController(IHistoricEventService historicEventService) {
        this.historicEventService = historicEventService;
    }

    @GetMapping("/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<HistoricEventDTO> getHistoricEventById(@PathVariable int viennaHistoryWikiId) {
        HistoricEventDTO eventDTO = historicEventService.getEventById(viennaHistoryWikiId);

        if (eventDTO != null) {
            return ResponseEntity.ok(eventDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by/name/{partialName}")
    public ResponseEntity<List<HistoricEventDTO>> getHistoricEventByPartialName(@PathVariable String partialName) {
        List<HistoricEventDTO> events = historicEventService.getEventByPartialName(partialName);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(events);
    }

    @GetMapping("/links/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<HistoricEventDTO>> getLinksById(@PathVariable int viennaHistoryWikiId) {
        List<HistoricEventDTO> events = historicEventService.getAllLinkedHistoricEventsById(viennaHistoryWikiId);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/links/buildings/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<HistoricBuildingDTO>> getLinkedBuildingsById(@PathVariable int viennaHistoryWikiId) {
        List<HistoricBuildingDTO> buildings = historicEventService.getAllLinkedHistoricBuildingsById(viennaHistoryWikiId);
        if (buildings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/links/persons/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<List<HistoricPersonDTO>> getLinkedPersonsById(@PathVariable int viennaHistoryWikiId) {
        List<HistoricPersonDTO> persons = historicEventService.getAllLinkedHistoricPersonsById(viennaHistoryWikiId);
        if (persons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(persons);
    }
}
