package group05.ase.openai.adapter.Service;

import group05.ase.openai.adapter.Controller.EnrichmentController;
import group05.ase.openai.adapter.dto.EnrichmentResponse;
import group05.ase.openai.adapter.dto.OpenAIResponseDTO;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private static final Logger logger = LoggerFactory.getLogger(EnrichmentController.class);

    // OpenAI API Key
    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .build();

    public EnrichmentResponse generateResponse(String tone, String content) {
        String summaryPrompt = "You are a historian writing a concise, factual summary of the following monument or for an academic audience. Around 200 words or less. ";
        String tonePrompt = switch (tone.toLowerCase()) {
            case "poetic" -> "You are a poet describing historical landmarks with vivid and emotional language.";
            case "tour" -> "You are a friendly tour guide presenting this information to visitors.";
            default -> "You are a historian writing a concise, factual information of the following historical site or figure for an academic audience.";
        };

        String summary = callOpenAI(enforceEnglish(summaryPrompt), content);
        String enrichedContent = callOpenAI(enforceEnglish(tonePrompt), content);

        return new EnrichmentResponse(summary, enrichedContent, tone);
    }

    private String enforceEnglish(String prompt) {
        return prompt.endsWith(".") ? prompt + " Always respond in English." : prompt + ". Always respond in English.";
    }

    private String callOpenAI(String systemPrompt, String content) {
        Map<String, Object> body = Map.of(
                "model", "gpt-4.1-nano",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", content)
                )
        );

        OpenAIResponseDTO response = webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(OpenAIResponseDTO.class)
                .block();

        logger.info("SERVICE: OpenAI response: {}", response.toString());
        return response.getChoices().get(0).getMessage().getContent();
    }
}
