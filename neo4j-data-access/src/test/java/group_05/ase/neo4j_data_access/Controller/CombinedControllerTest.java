package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.CombinedObject;
import group_05.ase.neo4j_data_access.Service.Implementation.CombinedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
public class CombinedControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private CombinedService combinedService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testGetCombinedObjectById_Found() throws Exception {
        // Setup
        CombinedObject sampleCombinedObject = new CombinedObject();
        sampleCombinedObject.setViennaHistoryWikiId(1);
        sampleCombinedObject.setName("Sample Name");
        sampleCombinedObject.setUrl("http://example.com");
        sampleCombinedObject.setImageUrls(List.of("http://image1.jpg", "http://image2.jpg"));

        Mockito.when(combinedService.getCombinedObjectById(1)).thenReturn(sampleCombinedObject);

        // Act & Assert
        mockMvc.perform(get("/api/combined/id/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viennaHistoryWikiId").value(1))
                .andExpect(jsonPath("$.name").value("Sample Name"))
                .andExpect(jsonPath("$.url").value("http://example.com"))
                .andExpect(jsonPath("$.imageUrls[0]").value("http://image1.jpg"))
                .andExpect(jsonPath("$.imageUrls[1]").value("http://image2.jpg"));
    }

    @Test
    void testGetCombinedObjectById_NotFound() throws Exception {
        // Setup
        Mockito.when(combinedService.getCombinedObjectById(999)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/combined/id/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
