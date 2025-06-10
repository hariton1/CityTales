package group_05.ase.neo4j_data_access.Services;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Service.Implementation.HistoricBuildingService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HistoricBuildingServiceTest {

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
    private HistoricBuildingService historicBuildingService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(neo4jProperties.getUrl()).thenReturn("bolt://localhost:7687");
        when(neo4jProperties.getUser()).thenReturn("user");
        when(neo4jProperties.getPassword()).thenReturn("pass");

        historicBuildingService = new HistoricBuildingService(neo4jProperties, mappingService);

        Field driverField = HistoricBuildingService.class.getDeclaredField("driver");
        driverField.setAccessible(true);
        driverField.set(historicBuildingService, driver);
    }

    @Test
    void getBuildingById_foundRecord_returnsDTO() {
        int testId = 42;
        ViennaHistoryWikiBuildingObject expectedDto = mock(ViennaHistoryWikiBuildingObject.class);
        Node mockNode = mock(Node.class);

        // Setup mock to simulate query result
        when(driver.session()).thenReturn(session);
        when(session.executeRead(any())).thenReturn(record);
        when(record.get("p")).thenReturn(value);
        when(value.asNode()).thenReturn(mockNode);
        when(mappingService.mapNodeToHistoricalBuildingEntity(mockNode)).thenReturn(expectedDto);

        ViennaHistoryWikiBuildingObject actual = historicBuildingService.getBuildingById(testId);

        assertNotNull(actual);
        assertEquals(expectedDto, actual);

        verify(session).executeRead(any());
        verify(mappingService).mapNodeToHistoricalBuildingEntity(mockNode);
    }

    @Test
    void getBuildingById_recordNotFound_returnsNull() {
        int testId = 999;

        when(driver.session()).thenReturn(session);
        when(session.executeRead(any())).thenThrow(new NoSuchRecordException("No record found"));

        ViennaHistoryWikiBuildingObject result = historicBuildingService.getBuildingById(testId);

        assertNull(result);
        verify(session).executeRead(any());
    }
}
