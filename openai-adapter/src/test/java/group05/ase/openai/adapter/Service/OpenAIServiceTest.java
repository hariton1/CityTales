package group05.ase.openai.adapter.Service;

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
    void generateResponse_shouldReturnEnrichmentDTO() {
        OpenAIService spyService = Mockito.spy(new OpenAIService());

        Mockito.doReturn("summary text")
                .doReturn("enriched tour guide text")
                .when(spyService)
                .callOpenAI(Mockito.anyString(), Mockito.anyString());

        var result = spyService.generateResponse("tour", "The monument was built in 1607.");

        assertEquals("summary text", result.getSummary());
        assertEquals("enriched tour guide text", result.getEnrichedContent());
        assertEquals("tour", result.getTone());
        Mockito.verify(spyService, Mockito.times(2))
                .callOpenAI(Mockito.anyString(), Mockito.anyString());
    }
}
