package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Implementation.HistoricEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
public class HistoricEventControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private HistoricEventService historicEventService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testGetHistoricEventById_Found() throws Exception {
        ViennaHistoryWikiEventObject event = new ViennaHistoryWikiEventObject();
        event.setViennaHistoryWikiId(1);
        event.setName("Revolution 1848");
        event.setUrl("http://example.com");
        event.setContentGerman("Inhalt");
        event.setContentEnglish("Content");

        Mockito.when(historicEventService.getEventById(1)).thenReturn(event);

        mockMvc.perform(get("/api/historicEvent/by/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viennaHistoryWikiId").value(1))
                .andExpect(jsonPath("$.name").value("Revolution 1848"));
    }

    @Test
    void testGetHistoricEventById_NotFound() throws Exception {
        Mockito.when(historicEventService.getEventById(999)).thenReturn(null);

        mockMvc.perform(get("/api/historicEvent/by/id/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetHistoricEventByPartialName_Found() throws Exception {
        ViennaHistoryWikiEventObject event = new ViennaHistoryWikiEventObject();
        event.setViennaHistoryWikiId(2);
        event.setName("Protest");

        Mockito.when(historicEventService.getEventByPartialName("pro"))
                .thenReturn(Collections.singletonList(event));

        mockMvc.perform(get("/api/historicEvent/by/name/pro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(2));
    }

    @Test
    void testGetHistoricEventByPartialName_Empty() throws Exception {
        Mockito.when(historicEventService.getEventByPartialName("xyz"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicEvent/by/name/xyz"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLinkedEventsById_Found() throws Exception {
        ViennaHistoryWikiEventObject linkedEvent = new ViennaHistoryWikiEventObject();
        linkedEvent.setViennaHistoryWikiId(3);
        linkedEvent.setName("Follow-up Protest");

        Mockito.when(historicEventService.getAllLinkedHistoricEventsById(1))
                .thenReturn(Collections.singletonList(linkedEvent));

        mockMvc.perform(get("/api/historicEvent/links/events/by/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(3));
    }

    @Test
    void testGetLinkedEventsById_Empty() throws Exception {
        Mockito.when(historicEventService.getAllLinkedHistoricEventsById(1))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicEvent/links/events/by/id/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLinkedBuildingsById_Found() throws Exception {
        ViennaHistoryWikiBuildingObject building = new ViennaHistoryWikiBuildingObject();
        building.setViennaHistoryWikiId(10);
        building.setName("Parliament");

        Mockito.when(historicEventService.getAllLinkedHistoricBuildingsById(1))
                .thenReturn(Collections.singletonList(building));

        mockMvc.perform(get("/api/historicEvent/links/buildings/by/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(10));
    }

    @Test
    void testGetLinkedBuildingsById_Empty() throws Exception {
        Mockito.when(historicEventService.getAllLinkedHistoricBuildingsById(1))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicEvent/links/buildings/by/id/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLinkedPersonsById_Found() throws Exception {
        ViennaHistoryWikiPersonObject person = new ViennaHistoryWikiPersonObject();
        person.setViennaHistoryWikiId(5);
        person.setName("Johann Strauss");

        Mockito.when(historicEventService.getAllLinkedHistoricPersonsById(1))
                .thenReturn(Collections.singletonList(person));

        mockMvc.perform(get("/api/historicEvent/links/persons/by/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viennaHistoryWikiId").value(5));
    }

    @Test
    void testGetLinkedPersonsById_Empty() throws Exception {
        Mockito.when(historicEventService.getAllLinkedHistoricPersonsById(1))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/historicEvent/links/persons/by/id/1"))
                .andExpect(status().isNoContent());
    }
}
