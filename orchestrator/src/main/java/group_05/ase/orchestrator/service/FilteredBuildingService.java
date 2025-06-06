package group_05.ase.orchestrator.service;

import group_05.ase.orchestrator.dto.BuildingDTO;
import group_05.ase.orchestrator.dto.UserInterestWithWeightDTO;
import group_05.ase.orchestrator.dto.ViennaHistoryWikiBuildingObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FilteredBuildingService {

    private final WebClient userDbClient;
    private final WebClient neo4jClient;

    public FilteredBuildingService(
            @Qualifier("userDbClient") WebClient userDbClient,
            @Qualifier("neo4jClient") WebClient neo4jClient) {
        this.userDbClient = userDbClient;
        this.neo4jClient = neo4jClient;
    }

    public List<BuildingDTO> getFilteredBuildingsForUser(double latitude, double longitude, double radius, UUID userId, String jwtToken) {
        List<UserInterestWithWeightDTO> interests = userDbClient
                .get()
                .uri("/userInterests/user/{userId}/interests/with-weight", userId)
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(UserInterestWithWeightDTO.class)
                .collectList()
                .block();

        List<String> interestNames = interests.stream()
                .map(UserInterestWithWeightDTO::getInterestNameEn)
                .collect(Collectors.toList());

        List<BuildingDTO> buildings = neo4jClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/buildings/by/location-and-categories")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("radius", radius)
                        .queryParam("categories", String.join(",", interestNames))
                        .build())
                .retrieve()
                .bodyToFlux(BuildingDTO.class)
                .collectList()
                .block();

        return buildings;
    }

    public BuildingDTO mapToBuildingDTO(ViennaHistoryWikiBuildingObject neo4jObj) {
        BuildingDTO dto = new BuildingDTO();
        dto.setViennaHistoryWikiId(neo4jObj.getViennaHistoryWikiId());
        dto.setName(neo4jObj.getName());
        dto.setUrl(neo4jObj.getUrl());
        dto.setBuildingType(neo4jObj.getBuildingType());
        dto.setLatitude(neo4jObj.getLatitude());
        dto.setLongitude(neo4jObj.getLongitude());
        return dto;
    }

}
