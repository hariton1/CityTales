package group_05.ase.neo4j_data_access.Services;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Implementation.HistoricPersonService;
import group_05.ase.neo4j_data_access.Service.Interface.IMappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HistoricPersonServiceTest {

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
    private HistoricPersonService historicPersonService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(neo4jProperties.getUrl()).thenReturn("bolt://localhost:7687");
        when(neo4jProperties.getUser()).thenReturn("user");
        when(neo4jProperties.getPassword()).thenReturn("pass");

        historicPersonService = new HistoricPersonService(neo4jProperties, mappingService);

        Field driverField = HistoricPersonService.class.getDeclaredField("driver");
        driverField.setAccessible(true);
        driverField.set(historicPersonService, driver);
    }

    @Test
    void getPersonById_foundRecord_returnsDTO() {
        int testId = 101;
        ViennaHistoryWikiPersonObject expectedDto = mock(ViennaHistoryWikiPersonObject.class);
        Node mockNode = mock(Node.class);

        when(driver.session()).thenReturn(session);
        when(session.executeRead(any())).thenReturn(record);
        when(record.get("p")).thenReturn(value);
        when(value.asNode()).thenReturn(mockNode);
        when(mappingService.mapNodeToPersonEntity(mockNode)).thenReturn(expectedDto);

        ViennaHistoryWikiPersonObject actual = historicPersonService.getPersonById(testId);

        assertNotNull(actual);
        assertEquals(expectedDto, actual);

        verify(session).executeRead(any());
        verify(mappingService).mapNodeToPersonEntity(mockNode);
    }

    @Test
    void getPersonById_recordNotFound_returnsNull() {
        int testId = 999;

        when(driver.session()).thenReturn(session);
        when(session.executeRead(any())).thenThrow(new NoSuchRecordException("No record found"));

        ViennaHistoryWikiPersonObject result = historicPersonService.getPersonById(testId);

        assertNull(result);
        verify(session).executeRead(any());
    }
}
