package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.CombinedObject;
import group_05.ase.neo4j_data_access.Service.Implementation.CombinedService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/combined")
public class CombinedController {

    private final CombinedService combinedService;

    public CombinedController(CombinedService combinedService) {
        this.combinedService = combinedService;
    }

    @GetMapping("/id/{viennaHistoryWikiId}")
    public ResponseEntity<CombinedObject> getCombinedObjectById(@PathVariable int viennaHistoryWikiId) {
        CombinedObject combinedDto = combinedService.getCombinedObjectById(viennaHistoryWikiId) ;

        if (combinedDto != null) {
            return ResponseEntity.ok(combinedDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
