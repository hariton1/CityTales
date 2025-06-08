package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Implementation.HistoricPersonService;
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
public class HistoricPersonControllerTest {


    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private HistoricPersonService historicPersonService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testGetHistoricPersonById_Found() throws Exception {
        ViennaHistoryWikiPersonObject person = new ViennaHistoryWikiPersonObject();
        person.setViennaHistoryWikiId(1);
        person.setName("Test Person");

        Mockito.when(historicPersonService.getPersonById(1)).thenReturn(person);

        mockMvc.perform(get("/api/historicPerson/by/id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viennaHistoryWikiId").value(1))
                .andExpect(jsonPath("$.name").value("Test Person"));
    }

    @Test
    void testGetHistoricPersonById_NotFound() throws Exception {
        Mockito.when(historicPersonService.getPersonById(999)).thenReturn(null);

        mockMvc.perform(get("/api/historicPerson/by/id/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetHistoricPersonsByPartialName_Found() throws Exception {
        ViennaHistoryWikiPersonObject person = new ViennaHistoryWikiPersonObject();
        person.setViennaHistoryWikiId(1);
        person.setName("Test Person");

        List<ViennaHistoryWikiPersonObject> people = List.of(person);
        Mockito.when(historicPersonService.getPersonsByPartialName("Test")).thenReturn(people);

        mockMvc.perform(get("/api/historicPerson/by/name/Test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Person"));
    }

    @Test
    void testGetHistoricPersonsByPartialName_NoContent() throws Exception {
        Mockito.when(historicPersonService.getPersonsByPartialName("None")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicPerson/by/name/None")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLinkedPersonsById_Found() throws Exception {
        ViennaHistoryWikiPersonObject linkedPerson = new ViennaHistoryWikiPersonObject();
        linkedPerson.setViennaHistoryWikiId(2);
        linkedPerson.setName("Linked Person");

        List<ViennaHistoryWikiPersonObject> linkedPersons = List.of(linkedPerson);
        Mockito.when(historicPersonService.getAllLinkedHistoricPersonsById(1)).thenReturn(linkedPersons);

        mockMvc.perform(get("/api/historicPerson/links/persons/by/id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(2))
                .andExpect(jsonPath("$[0].name").value("Linked Person"));
    }

    @Test
    void testGetLinkedPersonsById_NoContent() throws Exception {
        Mockito.when(historicPersonService.getAllLinkedHistoricPersonsById(999)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicPerson/links/persons/by/id/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLinkedBuildingsById_Found() throws Exception {
        ViennaHistoryWikiBuildingObject building = new ViennaHistoryWikiBuildingObject();
        building.setViennaHistoryWikiId(10);
        building.setName("Linked Building");

        List<ViennaHistoryWikiBuildingObject> buildings = List.of(building);
        Mockito.when(historicPersonService.getAllLinkedHistoricBuildingsById(1)).thenReturn(buildings);

        mockMvc.perform(get("/api/historicPerson/links/buildings/by/id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(10))
                .andExpect(jsonPath("$[0].name").value("Linked Building"));
    }

    @Test
    void testGetLinkedBuildingsById_NoContent() throws Exception {
        Mockito.when(historicPersonService.getAllLinkedHistoricBuildingsById(999)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicPerson/links/buildings/by/id/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLinkedEventsById_Found() throws Exception {
        ViennaHistoryWikiEventObject event = new ViennaHistoryWikiEventObject();
        event.setViennaHistoryWikiId(100);
        event.setName("Linked Event");

        List<ViennaHistoryWikiEventObject> events = List.of(event);
        Mockito.when(historicPersonService.getAllLinkedHistoricEventsById(1)).thenReturn(events);

        mockMvc.perform(get("/api/historicPerson/links/events/by/id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(100))
                .andExpect(jsonPath("$[0].name").value("Linked Event"));
    }

    @Test
    void testGetLinkedEventsById_NoContent() throws Exception {
        Mockito.when(historicPersonService.getAllLinkedHistoricEventsById(999)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicPerson/links/events/by/id/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
