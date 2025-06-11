package group_05.ase.orchestrator.controller;

import group_05.ase.orchestrator.client.ArticleClient;
import group_05.ase.orchestrator.dto.ArticleWeightDTO;
import group_05.ase.orchestrator.dto.ViennaHistoryWikiBuildingObject;
import group_05.ase.orchestrator.client.Neo4jBuildingClient;
import group_05.ase.orchestrator.client.UserInterestClient;
import group_05.ase.orchestrator.dto.UserInterestsDTO;
import group_05.ase.orchestrator.service.FilteredBuildingService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/buildings")
public class FilteredBuildingController {

    private final FilteredBuildingService filteredBuildingService;
    private final UserInterestClient userInterestClient;
    private final Neo4jBuildingClient buildingClient;
    private final ArticleClient articleClient;

    public FilteredBuildingController(FilteredBuildingService filteredBuildingService, UserInterestClient userInterestClient, Neo4jBuildingClient buildingClient, ArticleClient articleClient) {
        this.filteredBuildingService = filteredBuildingService;
        this.userInterestClient = userInterestClient;
        this.articleClient = articleClient;
        this.buildingClient = buildingClient;

    }

    @GetMapping("/filtered/byUser/{userId}")
    public ResponseEntity<List<ViennaHistoryWikiBuildingObject>> getFilteredBuildingsForUser(
            @PathVariable UUID userId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius
    ) {
        List<UserInterestsDTO> interests = userInterestClient.getUserInterests(userId);
        List<ViennaHistoryWikiBuildingObject> buildings = buildingClient.getBuildingsByLocation(latitude, longitude, radius);
        List<ArticleWeightDTO> articleWeights = articleClient.getAllArticleWeights();
        return ResponseEntity.ok(filteredBuildingService.filterBuildingsByUserInterests(buildings, interests, articleWeights));
    }

}
