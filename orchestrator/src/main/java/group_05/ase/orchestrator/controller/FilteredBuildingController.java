package group_05.ase.orchestrator.controller;

import group_05.ase.orchestrator.service.FilteredBuildingService;
import group_05.ase.orchestrator.dto.BuildingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class FilteredBuildingController {

    private final FilteredBuildingService filteredBuildingService;

    @GetMapping("/filtered")
    public List<BuildingDTO> getFilteredBuildingsForUser(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("radius") double radius,
            @RequestParam("userId") UUID userId,
            @RequestHeader("Authorization") String jwtToken
    ) {
        return filteredBuildingService.getFilteredBuildingsForUser(latitude, longitude, radius, userId, jwtToken);
    }


}
