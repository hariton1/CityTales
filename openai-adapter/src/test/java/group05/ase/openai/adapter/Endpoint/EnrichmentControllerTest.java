package group05.ase.openai.adapter.Endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import group05.ase.openai.adapter.Controller.EnrichmentController;
import group05.ase.openai.adapter.Service.OpenAIService;
import group05.ase.openai.adapter.dto.EnrichmentRequest;
import group05.ase.openai.adapter.dto.EnrichmentResponse;
import group05.ase.openai.adapter.dto.SummaryRequest;
import group05.ase.openai.adapter.dto.SummaryResponse;
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
    void generateSummary_shouldReturnBadRequestForEmptyContent() throws Exception {
        SummaryRequest request = new SummaryRequest();
        request.setContent("   ");

        mockMvc.perform(post("/api/enrich/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.summary").value("<p>Missing input for summary.</p>"));
    }

    @Test
    void generateSummary_shouldReturnSummaryResponse() throws Exception {
        String inputContent = "This monument was built in 1607.";
        String summaryHtml = "<p>Summary text</p><ul><li>Fact 1</li></ul>";

        SummaryRequest request = new SummaryRequest();
        request.setContent(inputContent);

        when(openAIService.generateSummary(inputContent))
                .thenReturn(new SummaryResponse(summaryHtml));

        mockMvc.perform(post("/api/enrich/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value(summaryHtml));
    }

    @Test
    void enrichText_shouldReturnBadRequestWhenToneIsMissing() throws Exception {
        EnrichmentRequest request = new EnrichmentRequest();
        request.setContent("The monument was built in 1607.");

        mockMvc.perform(post("/api/enrich/full")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.enrichedContent").value("<p><em>Missing content or tone.</em></p>"));
    }

    @Test
    void enrichText_shouldReturnBadRequestWhenContentIsMissing() throws Exception {
        EnrichmentRequest request = new EnrichmentRequest();
        request.setTone("tour");

        mockMvc.perform(post("/api/enrich/full")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.enrichedContent").value("<p><em>Missing content or tone.</em></p>"));
    }

    @Test
    void enrichText_shouldReturnEnrichmentResponse() throws Exception {
        String tone = "tour";
        String content = "The monument was built in 1607.";

        var mockResponse = new EnrichmentResponse(
                "Mocked enrichment", tone
        );

        when(openAIService.generateEnrichedContent(tone, content)).thenReturn(mockResponse);

        Map<String, String> requestBody = Map.of(
                "tone", tone,
                "content", content
        );

        mockMvc.perform(post("/api/enrich/full")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrichedContent").value("Mocked enrichment"))
                .andExpect(jsonPath("$.tone").value("tour"));
    }
}