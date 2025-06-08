package group_05.ase.orchestrator.client;

import group_05.ase.orchestrator.dto.UserInterestsDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.UUID;
import java.time.Duration;

@Component
public class UserInterestClient {

    private final WebClient webClient;

    public UserInterestClient(@Qualifier("userDbClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<UserInterestsDTO> getUserInterests(UUID userId) {
        try {
            return webClient.get()
                    .uri("userInterests/user/{userID}/interests/with-weight", userId)
                    .retrieve()
                    .bodyToFlux(UserInterestsDTO.class)
                    .collectList()
                    .block(Duration.ofSeconds(5));
        } catch (Exception e) {
            return List.of();
        }
    }
}
