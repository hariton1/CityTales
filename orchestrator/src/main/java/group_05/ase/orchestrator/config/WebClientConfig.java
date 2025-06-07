package group_05.ase.orchestrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userDbClient(@Value("${userdb.baseUrl}") String userDbBaseUrl) {
        return WebClient.builder()
                .baseUrl(userDbBaseUrl)
                .build();
    }

    @Bean
    public WebClient neo4jClient(@Value("${neo4j.baseUrl}") String neo4jBaseUrl) {
        return WebClient.builder()
                .baseUrl(neo4jBaseUrl)
                .build();
    }

}


