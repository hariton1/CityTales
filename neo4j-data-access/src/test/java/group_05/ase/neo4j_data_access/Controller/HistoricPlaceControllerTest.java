package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Implementation.HistoricBuildingService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
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

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class HistoricPlaceControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private HistoricBuildingService historicPlaceService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testGetHistoricalPlaceById_Found() throws Exception {
        ViennaHistoryWikiBuildingObject place = new ViennaHistoryWikiBuildingObject();
        place.setViennaHistoryWikiId(1);
        place.setName("Test Building");

        Mockito.when(historicPlaceService.getBuildingById(1)).thenReturn(place);

        mockMvc.perform(get("/api/historicPlace/by/id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viennaHistoryWikiId").value(1))
                .andExpect(jsonPath("$.name").value("Test Building"));
    }

    @Test
    void testGetHistoricalPlaceById_NotFound() throws Exception {
        Mockito.when(historicPlaceService.getBuildingById(999)).thenReturn(null);

        mockMvc.perform(get("/api/historicPlace/by/id/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetHistoricPlacesByPartialName_Found() throws Exception {
        ViennaHistoryWikiBuildingObject place = new ViennaHistoryWikiBuildingObject();
        place.setViennaHistoryWikiId(1);
        place.setName("Test Building");

        List<ViennaHistoryWikiBuildingObject> places = List.of(place);
        Mockito.when(historicPlaceService.getBuildingByPartialName("Test")).thenReturn(places);

        mockMvc.perform(get("/api/historicPlace/by/name/Test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Building"));
    }

    @Test
    void testGetHistoricPlacesByPartialName_NoContent() throws Exception {
        Mockito.when(historicPlaceService.getBuildingByPartialName("None")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicPlace/by/name/None")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetHistoricalPlacesByLocation_Found() throws Exception {
        ViennaHistoryWikiBuildingObject place = new ViennaHistoryWikiBuildingObject();
        place.setViennaHistoryWikiId(1);
        place.setName("Place Near Location");

        List<ViennaHistoryWikiBuildingObject> places = List.of(place);
        Mockito.when(historicPlaceService.findHistoricalBuildingWithinRadius(48.2, 16.3, 5.0)).thenReturn(places);

        mockMvc.perform(get("/api/historicPlace/by/location")
                        .param("latitude", "48.2")
                        .param("longitude", "16.3")
                        .param("radius", "5.0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(1))
                .andExpect(jsonPath("$[0].name").value("Place Near Location"));
    }

    @Test
    void testGetHistoricalPlacesByLocation_NoContent() throws Exception {
        Mockito.when(historicPlaceService.findHistoricalBuildingWithinRadius(0, 0, 1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicPlace/by/location")
                        .param("latitude", "0")
                        .param("longitude", "0")
                        .param("radius", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLinkedBuildingsById_Found() throws Exception {
        ViennaHistoryWikiBuildingObject linkedBuilding = new ViennaHistoryWikiBuildingObject();
        linkedBuilding.setViennaHistoryWikiId(2);
        linkedBuilding.setName("Linked Building");

        List<ViennaHistoryWikiBuildingObject> buildings = List.of(linkedBuilding);
        Mockito.when(historicPlaceService.getAllLinkedHistoricBuildingsById(1)).thenReturn(buildings);

        mockMvc.perform(get("/api/historicPlace/links/buildings/by/id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(2))
                .andExpect(jsonPath("$[0].name").value("Linked Building"));
    }

    @Test
    void testGetLinkedBuildingsById_NoContent() throws Exception {
        Mockito.when(historicPlaceService.getAllLinkedHistoricBuildingsById(999)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicPlace/links/buildings/by/id/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLinkedPersonsById_Found() throws Exception {
        ViennaHistoryWikiPersonObject person = new ViennaHistoryWikiPersonObject();
        person.setViennaHistoryWikiId(3);
        person.setName("Linked Person");

        List<ViennaHistoryWikiPersonObject> persons = List.of(person);
        Mockito.when(historicPlaceService.getAllLinkedHistoricPersonsById(1)).thenReturn(persons);

        mockMvc.perform(get("/api/historicPlace/links/persons/by/id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(3))
                .andExpect(jsonPath("$[0].name").value("Linked Person"));
    }

    @Test
    void testGetLinkedPersonsById_NoContent() throws Exception {
        Mockito.when(historicPlaceService.getAllLinkedHistoricPersonsById(999)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicPlace/links/persons/by/id/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLinkedEventsById_Found() throws Exception {
        ViennaHistoryWikiEventObject event = new ViennaHistoryWikiEventObject();
        event.setViennaHistoryWikiId(4);
        event.setName("Linked Event");

        List<ViennaHistoryWikiEventObject> events = List.of(event);
        Mockito.when(historicPlaceService.getAllLinkedHistoricEventsById(1)).thenReturn(events);

        mockMvc.perform(get("/api/historicPlace/links/events/by/id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(4))
                .andExpect(jsonPath("$[0].name").value("Linked Event"));
    }

    @Test
    void testGetLinkedEventsById_NoContent() throws Exception {
        Mockito.when(historicPlaceService.getAllLinkedHistoricEventsById(999)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicPlace/links/events/by/id/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
