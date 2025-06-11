package group_05.ase.orchestrator.client;

import group_05.ase.orchestrator.dto.ArticleWeightDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Component
public class ArticleClient {

    private final WebClient webClient;

    public ArticleClient(@Qualifier("userDbClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<ArticleWeightDTO> getAllArticleWeights() {
        ArticleWeightDTO[] result = webClient.get()
                .uri("/articleWeights")
                .retrieve()
                .bodyToMono(ArticleWeightDTO[].class)
                .block();
        return result != null ? Arrays.asList(result) : List.of();
    }
}


