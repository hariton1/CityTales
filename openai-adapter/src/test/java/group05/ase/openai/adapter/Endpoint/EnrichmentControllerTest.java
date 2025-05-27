package group05.ase.openai.adapter.Endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import group05.ase.openai.adapter.Controller.EnrichmentController;
import group05.ase.openai.adapter.Service.OpenAIService;
import group05.ase.openai.adapter.dto.EnrichmentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrichmentController.class)
@Import(EnrichmentControllerTest.MockConfig.class)
class EnrichmentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OpenAIService openAIService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public OpenAIService openAIService() {
            return Mockito.mock(OpenAIService.class);
        }
    }

    @BeforeEach
    void setup() {
        Mockito.reset(openAIService);
    }

    @Test
    void enrichText_shouldReturnEnrichmentResponse() throws Exception {
        String tone = "tour";
        String content = "The monument was built in 1607.";

        var mockResponse = new EnrichmentResponse(
                "Mocked summary", "Mocked enrichment", tone
        );

        when(openAIService.generateResponse(tone, content)).thenReturn(mockResponse);

        Map<String, String> requestBody = Map.of(
                "tone", tone,
                "content", content
        );

        mockMvc.perform(post("/api/enrich")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("Mocked summary"))
                .andExpect(jsonPath("$.enrichedContent").value("Mocked enrichment"))
                .andExpect(jsonPath("$.tone").value("tour"));
    }
}