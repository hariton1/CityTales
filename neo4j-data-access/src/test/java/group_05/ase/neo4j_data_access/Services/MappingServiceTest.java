package group_05.ase.neo4j_data_access.Services;

import group_05.ase.neo4j_data_access.Entity.*;
import group_05.ase.neo4j_data_access.Service.Implementation.MappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MappingServiceTest {

    private MappingService mappingService;

    @BeforeEach
    public void setup() {
        mappingService = new MappingService();
    }

    // Utility method to mock a node with a property
    private Node mockNodeWithProperties(Map<String, Value> props) {
        Node node = mock(Node.class);
        when(node.containsKey(anyString())).thenAnswer(invocation -> props.containsKey(invocation.getArgument(0)));
        when(node.get(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            return props.getOrDefault(key, mock(Value.class));
        });
        return node;
    }

    // ---------- mapNodeToPersonEntity ----------

    @Test
    public void testMapNodeToPersonEntity_positive() {
        Map<String, Value> props = new HashMap<>();
        props.put("viennaHistoryWikiId", mockIntValue(123));
        props.put("name", mockStringValue("John Doe"));
        props.put("contentGerman", mockStringValue("DE content"));
        props.put("contentEnglish", mockStringValue("EN content"));
        props.put("url", mockStringValue("https://example.com"));
        props.put("personName", mockStringValue("John"));
        props.put("links", mockListValue("link1", "link2"));
        props.put("imageUrls", mockListValue("img1", "img2"));

        Node node = mockNodeWithProperties(props);
        ViennaHistoryWikiPersonObject person = mappingService.mapNodeToPersonEntity(node);

        assertEquals(123, person.getViennaHistoryWikiId());
        assertEquals("John Doe", person.getName());
        assertEquals("John", person.getPersonName().orElse(null));
        assertEquals(2, person.getLinks().size());
    }

    @Test
    public void testMapNodeToPersonEntity_negative_missingOptionalFields() {
        Map<String, Value> props = new HashMap<>();
        props.put("viennaHistoryWikiId", mockIntValue(0));
        props.put("name", mockStringValue(""));
        props.put("contentGerman", mockStringValue(""));
        props.put("contentEnglish", mockStringValue(""));
        props.put("url", mockStringValue(""));

        Node node = mockNodeWithProperties(props);
        ViennaHistoryWikiPersonObject person = mappingService.mapNodeToPersonEntity(node);

        assertTrue(person.getLinks().isEmpty());
        assertEquals("N/A", person.getPersonName().orElse("N/A"));
    }

    // ---------- mapNodeToEventEntity ----------

    @Test
    public void testMapNodeToEventEntity_positive() {
        Map<String, Value> props = new HashMap<>();
        props.put("viennaHistoryWikiId", mockIntValue(1));
        props.put("name", mockStringValue("Event 1"));
        props.put("contentGerman", mockStringValue("DE"));
        props.put("contentEnglish", mockStringValue("EN"));
        props.put("url", mockStringValue("https://event"));
        props.put("dateFrom", mockStringValue("1900"));
        props.put("links", mockListValue("linkA"));

        Node node = mockNodeWithProperties(props);
        ViennaHistoryWikiEventObject event = mappingService.mapNodeToEventEntity(node);

        assertEquals("Event 1", event.getName());
        assertEquals("1900", event.getDateFrom().orElse(""));
    }

    @Test
    public void testMapNodeToEventEntity_negative_defaultsUsed() {
        Map<String, Value> props = new HashMap<>();
        props.put("viennaHistoryWikiId", mockIntValue(0));
        props.put("name", mockStringValue(""));

        Node node = mockNodeWithProperties(props);
        ViennaHistoryWikiEventObject event = mappingService.mapNodeToEventEntity(node);

        assertEquals("N/A", event.getDateFrom().orElse("N/A"));
        assertTrue(event.getLinks().isEmpty());
    }

    // ---------- mapNodeToHistoricalBuildingEntity ----------

    @Test
    public void testMapNodeToHistoricalBuildingEntity_positive() {
        Map<String, Value> props = new HashMap<>();
        props.put("viennaHistoryWikiId", mockIntValue(55));
        props.put("name", mockStringValue("Historic House"));
        props.put("contentGerman", mockStringValue("Haus"));
        props.put("contentEnglish", mockStringValue("House"));
        props.put("url", mockStringValue("https://building"));
        props.put("latitude", mockDoubleValue(48.2));
        props.put("longitude", mockDoubleValue(16.3));
        props.put("imageUrls", mockListValue("img1.jpg"));

        Node node = mockNodeWithProperties(props);
        ViennaHistoryWikiBuildingObject building = mappingService.mapNodeToHistoricalBuildingEntity(node);

        assertEquals("Historic House", building.getName());
        assertEquals(48.2, building.getLatitude().orElse(0.0));
        assertEquals("img1.jpg", building.getImageUrls().get(0));
    }

    @Test
    public void testMapNodeToHistoricalBuildingEntity_negative_missingCoords() {
        Map<String, Value> props = new HashMap<>();
        props.put("viennaHistoryWikiId", mockIntValue(0));
        props.put("name", mockStringValue(""));

        Node node = mockNodeWithProperties(props);
        ViennaHistoryWikiBuildingObject building = mappingService.mapNodeToHistoricalBuildingEntity(node);

        assertTrue(building.getLatitude().isEmpty());
        assertTrue(building.getLongitude().isEmpty());
    }

    // ---------- mapNodeToCombinedObject ----------

    @Test
    public void testMapNodeToCombinedObject_positive() {
        Map<String, Value> props = new HashMap<>();
        props.put("viennaHistoryWikiId", mockIntValue(77));
        props.put("name", mockStringValue("Combined Name"));
        props.put("url", mockStringValue("https://combined"));
        props.put("imageUrls", mockListValue("image1"));

        Node node = mockNodeWithProperties(props);
        CombinedObject obj = mappingService.mapNodeToCombinedObject(node);

        assertEquals(77, obj.getViennaHistoryWikiId());
        assertEquals("Combined Name", obj.getName());
    }

    @Test
    public void testMapNodeToCombinedObject_negative_missingImageUrls() {
        Map<String, Value> props = new HashMap<>();
        props.put("viennaHistoryWikiId", mockIntValue(88));
        props.put("name", mockStringValue("No Image"));
        props.put("url", mockStringValue("https://noimage"));

        Node node = mockNodeWithProperties(props);
        CombinedObject obj = mappingService.mapNodeToCombinedObject(node);

        assertTrue(obj.getImageUrls().isEmpty());
    }

    // ---------- Value mocks ----------

    private Value mockStringValue(String value) {
        Value mock = mock(Value.class);
        when(mock.asString()).thenReturn(value);
        return mock;
    }

    private Value mockIntValue(int value) {
        Value mock = mock(Value.class);
        when(mock.asInt()).thenReturn(value);
        return mock;
    }

    private Value mockDoubleValue(double value) {
        Value mock = mock(Value.class);
        when(mock.asDouble()).thenReturn(value);
        return mock;
    }

    private Value mockListValue(String... elements) {
        Value mock = mock(Value.class);
        when(mock.asList((Function<Value, String>) any())).thenReturn(Arrays.asList(elements));
        return mock;
    }
}
