package group_05.ase.neo4j_data_access.Services;

import group_05.ase.neo4j_data_access.DTO.FunFactCardDTO;
import group_05.ase.neo4j_data_access.DTO.FunFactResult;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FunFactMapperTest {

    @Test
    void testMapToFunFactCard_Building() {
        var building = new ViennaHistoryWikiBuildingObject();
        building.setName("Stephansdom");
        building.setImageUrls(Arrays.asList("bild1.jpg", "bild2.jpg"));

        var result = new FunFactResult("Er wurde im 12. Jahrhundert erbaut.", 0.95, "Superlativ und Jahreszahl gefunden.");
        FunFactCardDTO dto = FunFactMapper.mapToFunFactCard(building, result);

        assertEquals("Stephansdom", dto.getHeadline());
        assertEquals("Er wurde im 12. Jahrhundert erbaut.", dto.getFunFact());
        assertEquals("bild1.jpg", dto.getImageUrl());
        assertEquals(0.95, dto.getScore());
        assertEquals("Superlativ und Jahreszahl gefunden.", dto.getReason());
    }

    @Test
    void testMapToFunFactCard_Person() {
        var person = new ViennaHistoryWikiPersonObject();
        person.setName("Sigmund Freud");
        person.setImageUrls(Collections.singletonList("freud.jpg"));

        var result = new FunFactResult("Begründer der Psychoanalyse.", 0.88, "Bekanntheitsgrad erkannt.");
        FunFactCardDTO dto = FunFactMapper.mapToFunFactCard(person, result);

        assertEquals("Sigmund Freud", dto.getHeadline());
        assertEquals("Begründer der Psychoanalyse.", dto.getFunFact());
        assertEquals("freud.jpg", dto.getImageUrl());
        assertEquals(0.88, dto.getScore());
        assertEquals("Bekanntheitsgrad erkannt.", dto.getReason());
    }

    @Test
    void testMapToFunFactCard_Event() {
        var event = new ViennaHistoryWikiEventObject();
        event.setName("Wiener Kongress");
        event.setImageUrls(Arrays.asList("kongress.jpg"));

        var result = new FunFactResult("Prägte Europas Grenzen 1815.", 0.90, "Jahreszahl und Wirkung erkannt.");
        FunFactCardDTO dto = FunFactMapper.mapToFunFactCard(event, result);

        assertEquals("Wiener Kongress", dto.getHeadline());
        assertEquals("Prägte Europas Grenzen 1815.", dto.getFunFact());
        assertEquals("kongress.jpg", dto.getImageUrl());
        assertEquals(0.90, dto.getScore());
        assertEquals("Jahreszahl und Wirkung erkannt.", dto.getReason());
    }
}
