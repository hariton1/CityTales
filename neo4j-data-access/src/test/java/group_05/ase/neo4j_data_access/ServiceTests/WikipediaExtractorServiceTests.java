package group_05.ase.neo4j_data_access.ServiceTests;

import group_05.ase.neo4j_data_access.Service.Implementation.WikipediaExtractorService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WikipediaExtractorServiceTests {
    WikipediaExtractorService service = new WikipediaExtractorService();

    @Test
    void testGetFirstParagraph_validUrl() {
        String result = service.getFirstParagraph("https://en.wikipedia.org/wiki/Wolfgang_Amadeus_Mozart");
        assertNotNull(result);
        assertFalse(result.contains("Error"));
    }

    @Test
    void testGetFirstParagraph_invalidUrl() {
        String result = service.getFirstParagraph("https://thispagedoesnotexist.com");
        assertTrue(result.contains("Error"));
    }
}
