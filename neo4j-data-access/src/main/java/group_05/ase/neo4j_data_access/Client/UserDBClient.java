package group_05.ase.neo4j_data_access.Client;


import group_05.ase.neo4j_data_access.Entity.Tour.PriceDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.UUID;
import java.time.Duration;

@Component
public class UserDBClient {

    private final WebClient webClient;

    public UserDBClient(@Qualifier("userDbClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Integer> getUserInterestsIds(UUID userId) {
        try {
            return webClient.get()
                    .uri("userInterests/interest_ids_user_id=" + userId)
                    .retrieve()
                    .bodyToFlux(Integer.class)
                    .collectList()
                    .block(Duration.ofSeconds(5));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }

    public List<PriceDTO> getPricesFromDB(List<Integer> location_ids) {
        try {
            return webClient.post()
                    .uri("prices/find/multiple")
                    .bodyValue(location_ids)
                    .retrieve()
                    .bodyToFlux(PriceDTO.class)
                    .collectList()
                    .block(Duration.ofSeconds(5));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }
}

