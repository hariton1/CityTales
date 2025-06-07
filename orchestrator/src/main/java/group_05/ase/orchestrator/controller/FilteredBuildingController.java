package group_05.ase.orchestrator.controller;

import group_05.ase.orchestrator.dto.ViennaHistoryWikiBuildingObject;
import group_05.ase.orchestrator.client.Neo4jBuildingClient;
import group_05.ase.orchestrator.client.UserInterestClient;
import group_05.ase.orchestrator.dto.UserInterestsDTO;
import group_05.ase.orchestrator.service.FilteredBuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class FilteredBuildingController {

    private final FilteredBuildingService filteredBuildingService;
    private final UserInterestClient userInterestClient;
    private final Neo4jBuildingClient buildingClient;

//    @GetMapping("/filtered/byUserAndLocation")
//    public List<ViennaHistoryWikiBuildingObject> getFilteredBuildingsForUserAndLocation(
//            @RequestParam UUID userId,
//            @RequestParam double latitude,
//            @RequestParam double longitude,
//            @RequestParam double radius
//    ) {
//        try {
//            List<UserInterestsDTO> interests = userInterestClient.getUserInterests(userId);
//            List<ViennaHistoryWikiBuildingObject> buildings = buildingClient.getBuildingsByLocation(latitude, longitude, radius);
//            return filteredBuildingService.filterBuildingsByUserInterestsAndLocation(buildings, interests, latitude, longitude, radius);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Fehler im Controller: " + e.getMessage(), e);
//        }
//    }

    @GetMapping("/filtered/byUser/{userId}")
    public List<ViennaHistoryWikiBuildingObject> getFilteredBuildingsForUser(
            @PathVariable UUID userId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius
    ) {
        List<UserInterestsDTO> interests = userInterestClient.getUserInterests(userId);
        List<ViennaHistoryWikiBuildingObject> buildings = buildingClient.getBuildingsByLocation(latitude, longitude, radius);
        return filteredBuildingService.filterBuildingsByUserInterests(buildings, interests);
    }

}
