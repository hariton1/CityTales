package group_05.ase.neo4j_data_access.Client;

import group_05.ase.neo4j_data_access.Entity.Tour.MatchRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.time.Duration;

@Component("qdrantClientComponent")
public class QdrantClient {

    private final WebClient webClient;

    public QdrantClient(@Qualifier("qdrantClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Integer> getBuildingEntityIdsFromQdrant(MatchRequest matchRequestDTO) {
        try {
            return webClient.post()
                    .uri("/categorize/match")
                    .bodyValue(matchRequestDTO)
                    .retrieve()
                    .bodyToFlux(Integer.class)
                    .collectList()
                    .block(Duration.ofSeconds(5));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }
}
