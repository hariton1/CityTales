package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactExtractorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FunFactController.class)
class FunFactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IHistoricBuildingService historicBuildingService;

    @MockBean
    private FunFactExtractorService funFactExtractorService;

    @Test
    void getFunFactForBuilding_returnsFunFactCard() throws Exception {
        ViennaHistoryWikiBuildingObject building = new ViennaHistoryWikiBuildingObject();
        building.setName("Prater");
        building.setImageUrls(Arrays.asList("prater.jpg"));
        building.setContentGerman("Der Prater ist ein berühmter Park in Wien. Hier steht das berühmte Riesenrad.");

        Mockito.when(historicBuildingService.getBuildingById(anyInt())).thenReturn(building);
        Mockito.when(funFactExtractorService.extractFunFactHybridWithReason(Mockito.anyString()))
                .thenReturn(new group_05.ase.neo4j_data_access.DTO.FunFactResult(
                        "Hier steht das berühmte Riesenrad.", 2.5, "Heuristik: Zahl/Superlativ, "));

        mockMvc.perform(get("/api/funfact/by/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headline").value("Prater"))
                .andExpect(jsonPath("$.funFact").value("Hier steht das berühmte Riesenrad."))
                .andExpect(jsonPath("$.imageUrl").value("prater.jpg"));
    }
}
