package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.ITourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group_05.ase.neo4j_data_access.Service.Interface.ISearchService;

import java.util.List;

@RestController
@RequestMapping("/api/tour")
public class TourController {

    private final ITourService tourService;

    public TourController(ITourService tourService) {
        this.tourService = tourService;
    }
}
