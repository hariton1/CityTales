package group_05.ase.neo4j_data_access.Services;

import group_05.ase.neo4j_data_access.DTO.FunFactResult;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactExtractorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.junit.jupiter.api.Assertions.*;

class FunFactExtractorServiceTest {

    private FunFactExtractorService service;
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        service = new FunFactExtractorService(restTemplate);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testExtractFunFactWithML() {
        String testText = "Die k. k. Wiener Gartenbaugesellschaft wurde am 2. Mai 1837 gegründet. Es gab berühmte Bälle, ein Kino, einen Skandal und eine Hundeshow.";
        String expectedFunFact = "Es gab berühmte Bälle, ein Kino, einen Skandal und eine Hundeshow.";
        double expectedScore = 0.5;

        // Mock the Python service response
        mockServer.expect(requestTo("http://funfact-ml-service:5005/funfact"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess(
                        "{ \"funfact\": \"" + expectedFunFact + "\", \"score\": " + expectedScore + " }",
                        MediaType.APPLICATION_JSON
                ));

        FunFactResult result = service.extractFunFactWithML(testText);

        assertThat(result.getSentence()).isEqualTo(expectedFunFact);
        assertThat(result.getScore()).isEqualTo(expectedScore);

        mockServer.verify();
    }

    @Test
    void extractFunFactHybridWithReason_returnsExpectedResult() {
        String text = "Das Schloss ist das älteste Gebäude der Stadt. Es spukt angeblich dort. Viele Touristen besuchen es jedes Jahr.";
        FunFactResult result = service.extractFunFactHybridWithReason(text);

        assertNotNull(result.getSentence());
        assertFalse(result.getSentence().isEmpty());
        assertTrue(result.getScore() > 0);
        assertNotNull(result.getReason());
        assertFalse(result.getReason().isEmpty());
        assertTrue(
                result.getSentence().contains("älteste") ||
                        result.getSentence().contains("spukt") ||
                        result.getSentence().contains("Touristen")
        );
    }

    @Test
    void extractFunFactHybridWithReason_emptyText() {
        FunFactResult result = service.extractFunFactHybridWithReason("");
        assertEquals("", result.getSentence());
        assertEquals(0, result.getScore());
        assertTrue(result.getReason().toLowerCase().contains("kein"));
    }
}
