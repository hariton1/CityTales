package group_05.ase.orchestrator.client;

import group_05.ase.orchestrator.dto.UserInterestsDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
public class UserInterestClient {

    private final WebClient webClient;

    public UserInterestClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://user-db:8090/api/userInterest").build();
    }

    public List<UserInterestsDTO> getUserInterests(UUID userId) {
        // Annahme: Endpoint gibt List<UserInterestDTO> zurück
        return webClient.get()
                .uri("/byUserId/{userId}", userId)
                .retrieve()
                .bodyToFlux(UserInterestsDTO.class)
                .collectList()
                .block();
    }
}
