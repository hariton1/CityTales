package group05.ase.openai.adapter.Service;

import group05.ase.openai.adapter.dto.EnrichmentResponse;
import group05.ase.openai.adapter.dto.SummaryResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAIServiceTest {
    private final OpenAIService service = new OpenAIService();

    @Test
    void enforceEnglish_shouldAppendInstruction() {
        String input = "You are a historian.";
        String result = service.enforceEnglish(input);
        assertTrue(result.endsWith("Always respond in English."));
    }

    @Test
    void generateSummary_shouldReturnExpectedSummary() {
        // Arrange
        OpenAIService spyService = Mockito.spy(new OpenAIService());

        Mockito.doReturn("<p>Mocked summary</p><ul><li>Fact 1</li></ul>")
                .when(spyService)
                .callOpenAI(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        String content = "The monument was built in 1607.";
        SummaryResponse result = spyService.generateSummary(content);

        assertEquals("<p>Mocked summary</p><ul><li>Fact 1</li></ul>", result.getSummary());

        Mockito.verify(spyService, Mockito.times(1))
                .callOpenAI(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void generateEnrichedContent_shouldReturnEnrichmentDTO() {
        OpenAIService spyService = Mockito.spy(new OpenAIService());

        Mockito.doReturn("enriched tour guide text")
                .when(spyService)
                .callOpenAI(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        EnrichmentResponse result = spyService.generateEnrichedContent("tour", "The monument was built in 1607.");

        assertEquals("tour", result.getTone());
        assertEquals("enriched tour guide text", result.getEnrichedContent());
        Mockito.verify(spyService, Mockito.times(1))
                .callOpenAI(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }
}
