package group_05.ase.orchestrator.client;

import group_05.ase.orchestrator.dto.ViennaHistoryWikiBuildingObject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class Neo4jBuildingClient {

    private final WebClient webClient;

    public Neo4jBuildingClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://neo4j-service:8093/api/buildings").build();
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
