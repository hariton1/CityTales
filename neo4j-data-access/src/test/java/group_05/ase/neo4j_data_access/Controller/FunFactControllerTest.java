package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.DTO.FunFactCardDTO;
import group_05.ase.neo4j_data_access.DTO.FunFactResult;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactExtractorService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricEventService;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricPersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FunFactController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FunFactControllerTest {

    @MockBean
    private IHistoricBuildingService historicBuildingService;
    @MockBean
    private FunFactExtractorService funFactExtractorService;
    @MockBean
    private IHistoricPersonService historicPersonService;
    @MockBean
    private IHistoricEventService historicEventService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetFunFactForBuilding() throws Exception {
        ViennaHistoryWikiBuildingObject building = new ViennaHistoryWikiBuildingObject();
        building.setContentGerman("Das ist ein Dummytext für das Gebäude.");

        FunFactResult funFactResult = new FunFactResult("Super Headline", 0.95,"Ein toller Grund");
        // Angenommen FunFactCardDTO benötigt: headline, funFact, source, score, reason
        FunFactCardDTO card = new FunFactCardDTO("Super Headline", "Eine tolle FunFact", "Wikipedia", 0.95, "Weil das Gebäude berühmt ist");

        when(historicBuildingService.getBuildingById(9356)).thenReturn(building);
        when(funFactExtractorService.extractFunFactWithML(building.getContentGerman())).thenReturn(funFactResult);

        mockMvc.perform(get("/api/funfact/building/9356"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sentence").exists())
                .andExpect(jsonPath("$.score").exists())
                .andExpect(jsonPath("$.reason").exists());
    }
}