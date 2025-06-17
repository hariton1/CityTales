package group_05.ase.neo4j_data_access.Client;


import group_05.ase.neo4j_data_access.Entity.Tour.PriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.UUID;
import java.time.Duration;

@Component
public class UserDBClient {

    private final WebClient webClient;

    private static final Logger logger = LoggerFactory.getLogger(UserDBClient.class);

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
            logger.error("Error fetching user interests ids from userdb client: {}", e.getMessage());
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
            logger.error("Error fetching prices ids from userdb client: {}", e.getMessage());
            return List.of();
        }
    }
}

