package group_05.ase.neo4j_data_access.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.neo4j_data_access.Entity.Tour.DurationDistanceEstimateDTO;
import group_05.ase.neo4j_data_access.Service.Interface.ITourService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TourControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private ITourService tourService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testGetDurationDistanceEstimate_Found() throws Exception {
        DurationDistanceEstimateDTO request = new DurationDistanceEstimateDTO();
        request.setStart_lat(48.2);
        request.setStart_lng(16.3);
        request.setEnd_lat(48.3);
        request.setEnd_lng(16.4);
        request.setStops(List.of());

        Map<String, Double> response = new HashMap<>();
        response.put("distance", 3.0);
        response.put("duration", 15.0);

        Mockito.when(tourService.getDurationDistanceEstimate(
                        request.getStart_lat(), request.getStart_lng(),
                        request.getEnd_lat(), request.getEnd_lng(), request.getStops()))
                .thenReturn(response);

        mockMvc.perform(post("/api/tour/durationDistanceEstimate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance").value(3.0))
                .andExpect(jsonPath("$.duration").value(15.0));
    }

    @Test
    void testGetDurationDistanceEstimate_NoContent() throws Exception {
        DurationDistanceEstimateDTO request = new DurationDistanceEstimateDTO();
        request.setStart_lat(0.0);
        request.setStart_lng(0.0);
        request.setEnd_lat(0.0);
        request.setEnd_lng(0.0);
        request.setStops(List.of());

        Mockito.when(tourService.getDurationDistanceEstimate(
                        request.getStart_lat(), request.getStart_lng(),
                        request.getEnd_lat(), request.getEnd_lng(), request.getStops()))
                .thenReturn(null);

        mockMvc.perform(post("/api/tour/durationDistanceEstimate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetDurationDistanceMatrix_Found() throws Exception {
        DurationDistanceEstimateDTO request = new DurationDistanceEstimateDTO();
        request.setStart_lat(48.2);
        request.setStart_lng(16.3);
        request.setEnd_lat(48.3);
        request.setEnd_lng(16.4);
        request.setStops(List.of());

        List<List<Float>> matrix = List.of(
                List.of(0.0f, 1.2f),
                List.of(1.2f, 0.0f)
        );
        Map<String, List<List<Float>>> response = new HashMap<>();
        response.put("distanceMatrix", matrix);

        Mockito.when(tourService.getDurationDistanceMatrix(
                        request.getStart_lat(), request.getStart_lng(),
                        request.getEnd_lat(), request.getEnd_lng(), request.getStops()))
                .thenReturn(response);

        mockMvc.perform(post("/api/tour/durationDistanceMatrix")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanceMatrix[0][0]").value(0.0))
                .andExpect(jsonPath("$.distanceMatrix[0][1]").value(1.2));
    }

    @Test
    void testGetDurationDistanceMatrix_NoContent() throws Exception {
        DurationDistanceEstimateDTO request = new DurationDistanceEstimateDTO();
        request.setStart_lat(0.0);
        request.setStart_lng(0.0);
        request.setEnd_lat(0.0);
        request.setEnd_lng(0.0);
        request.setStops(List.of());

        Mockito.when(tourService.getDurationDistanceMatrix(
                        request.getStart_lat(), request.getStart_lng(),
                        request.getEnd_lat(), request.getEnd_lng(), request.getStops()))
                .thenReturn(null);

        mockMvc.perform(post("/api/tour/durationDistanceMatrix")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}

