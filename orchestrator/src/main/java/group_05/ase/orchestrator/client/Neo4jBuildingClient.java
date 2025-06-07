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

    public List<ViennaHistoryWikiBuildingObject> getAllBuildings() {
        return webClient.get()
                .uri("/all")
                .retrieve()
                .bodyToFlux(ViennaHistoryWikiBuildingObject.class)
                .collectList()
                .block();
    }
}
