package group_05.ase.neo4j_data_access.Config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private ExchangeFilterFunction jwtFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String token = resolveJwtToken();
            if (token != null) {
                var newRequest = org.springframework.web.reactive.function.client.ClientRequest.from(clientRequest)
                        .header("Authorization", "Bearer " + token)
                        .build();
                return reactor.core.publisher.Mono.just(newRequest);
            }
            return reactor.core.publisher.Mono.just(clientRequest);
        });
    }

    private String resolveJwtToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getTokenValue();
        }
        return null;
    }

    @Bean
    public WebClient userDbClient(@Value("${userdb.baseUrl}") String userDbBaseUrl) {
        return WebClient.builder()
                .baseUrl(userDbBaseUrl)
                .filter(jwtFilter())
                .build();
    }

    @Bean
    public WebClient qdrantClient(@Value("${qdrant.baseUrl}") String qdrantBaseUrl) {
        return WebClient.builder()
                .baseUrl(qdrantBaseUrl)
                .filter(jwtFilter())
                .build();
    }
}

