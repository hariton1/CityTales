package group_05.ase.neo4j_data_access.Services;

import group_05.ase.neo4j_data_access.DTO.FunFactResult;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactExtractorService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunFactExtractorServiceTest {

    private final FunFactExtractorService service = new FunFactExtractorService();

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
