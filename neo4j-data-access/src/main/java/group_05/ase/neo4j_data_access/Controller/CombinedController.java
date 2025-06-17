package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.CombinedObject;
import group_05.ase.neo4j_data_access.Service.Implementation.CombinedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/combined")
public class CombinedController {

    private final CombinedService combinedService;

    private static final Logger logger = LoggerFactory.getLogger(CombinedController.class);

    public CombinedController(CombinedService combinedService) {
        this.combinedService = combinedService;
    }

    @GetMapping("/id/{viennaHistoryWikiId}")
    public ResponseEntity<CombinedObject> getCombinedObjectById(@PathVariable int viennaHistoryWikiId) {
        CombinedObject combinedDto = combinedService.getCombinedObjectById(viennaHistoryWikiId) ;
        logger.info("Fetching combined object with id: {}", viennaHistoryWikiId);
        if (combinedDto != null) {
            return ResponseEntity.ok(combinedDto);
        } else {
            logger.error("Combined object with id {} not found", viennaHistoryWikiId);
            return ResponseEntity.notFound().build();
        }
    }
}
