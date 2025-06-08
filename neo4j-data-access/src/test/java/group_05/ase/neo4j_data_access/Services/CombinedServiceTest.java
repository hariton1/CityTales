package group_05.ase.neo4j_data_access.Services;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.Entity.CombinedObject;
import group_05.ase.neo4j_data_access.Service.Implementation.CombinedService;
import group_05.ase.neo4j_data_access.Service.Implementation.MappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CombinedServiceTest {
    @Mock
    private Neo4jProperties neo4jProperties;

    @Mock
    private MappingService mappingService;

    @Mock
    private Driver driver;

    @Mock
    private Session session;

    @Mock
    private Record record;

    @Mock
    private Value value;

    @InjectMocks
    private CombinedService combinedService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(neo4jProperties.getUrl()).thenReturn("bolt://localhost:7687");
        when(neo4jProperties.getUser()).thenReturn("user");
        when(neo4jProperties.getPassword()).thenReturn("pass");

        combinedService = new CombinedService(neo4jProperties, mappingService);

        Field driverField = CombinedService.class.getDeclaredField("driver");
        driverField.setAccessible(true);
        driverField.set(combinedService, driver);
    }


    @Test
    void getCombinedObjectById_foundRecord_returnsCombinedObject() {

        // Setup
        int testId = 123;
        CombinedObject expectedCombinedObject = mock(CombinedObject.class);
        Node mockNode = mock(Node.class);

        when(driver.session()).thenReturn(session);
        when(session.executeRead(any())).thenReturn(record);
        when(record.get("n")).thenReturn(value);
        when(value.asNode()).thenReturn(mockNode);
        when(mappingService.mapNodeToCombinedObject(mockNode)).thenReturn(expectedCombinedObject);

        // Act
        CombinedObject actual = combinedService.getCombinedObjectById(testId);

        // Verify
        assertNotNull(actual);
        assertEquals(expectedCombinedObject, actual);

        verify(driver).session();
        verify(session).executeRead(any());
        verify(record).get("n");
        verify(mappingService).mapNodeToCombinedObject(mockNode);
    }
}
