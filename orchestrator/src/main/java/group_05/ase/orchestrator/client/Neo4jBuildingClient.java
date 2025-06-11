package group_05.ase.orchestrator.client;

import group_05.ase.orchestrator.dto.ViennaHistoryWikiBuildingObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class Neo4jBuildingClient {

    private final WebClient webClient;

    public Neo4jBuildingClient(@Qualifier("neo4jClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // Im Neo4jBuildingClient:
    public List<ViennaHistoryWikiBuildingObject> getBuildingsByLocation(double latitude, double longitude, double radius) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/historicPlace/by/location")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("radius", radius)
                        .build())
                .retrieve()
                .bodyToFlux(ViennaHistoryWikiBuildingObject.class)
                .collectList()
                .block();
    }

}
