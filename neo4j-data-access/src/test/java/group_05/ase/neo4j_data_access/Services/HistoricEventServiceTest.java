package group_05.ase.neo4j_data_access.Services;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Service.Implementation.HistoricEventService;
import group_05.ase.neo4j_data_access.Service.Interface.IMappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HistoricEventServiceTest {

    @Mock
    private Neo4jProperties neo4jProperties;

    @Mock
    private IMappingService mappingService;

    @Mock
    private Driver driver;

    @Mock
    private Session session;

    @Mock
    private Record record;

    @Mock
    private Value value;

    @InjectMocks
    private HistoricEventService historicEventService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(neo4jProperties.getUrl()).thenReturn("bolt://localhost:7687");
        when(neo4jProperties.getUser()).thenReturn("user");
        when(neo4jProperties.getPassword()).thenReturn("pass");

        historicEventService = new HistoricEventService(neo4jProperties, mappingService);

        Field driverField = HistoricEventService.class.getDeclaredField("driver");
        driverField.setAccessible(true);
        driverField.set(historicEventService, driver);
    }

    @Test
    void getEventById_foundRecord_returnsDTO() {
        int testId = 42;
        ViennaHistoryWikiEventObject expectedDto = mock(ViennaHistoryWikiEventObject.class);
        Node mockNode = mock(Node.class);

        when(driver.session()).thenReturn(session);
        when(session.executeRead(any())).thenReturn(record);
        when(record.get("p")).thenReturn(value);
        when(value.asNode()).thenReturn(mockNode);
        when(mappingService.mapNodeToEventEntity(mockNode)).thenReturn(expectedDto);

        ViennaHistoryWikiEventObject actual = historicEventService.getEventById(testId);

        assertNotNull(actual);
        assertEquals(expectedDto, actual);

        verify(session).executeRead(any());
        verify(mappingService).mapNodeToEventEntity(mockNode);
    }

    @Test
    void getEventById_recordNotFound_returnsNull() {
        int testId = 999;

        when(driver.session()).thenReturn(session);
        when(session.executeRead(any())).thenThrow(new NoSuchRecordException("No record found"));

        ViennaHistoryWikiEventObject result = historicEventService.getEventById(testId);

        assertNull(result);
        verify(session).executeRead(any());
    }
}
