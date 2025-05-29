package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.ArticleWeightDTO;
import group_05.ase.user_db.services.ArticleWeightService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ArticleWeightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    ArticleWeightService articleWeightService;

    private final ArticleWeightDTO articleWeightDTO = new ArticleWeightDTO (
            1,
            10000,
            0.01F
    );

    private final ArrayList<ArticleWeightDTO> articleWeightDTOs = new ArrayList<>(List.of(articleWeightDTO));

    @Test
    public void testGetAllArticleWeights() throws Exception {

        when(articleWeightService.getAllArticleWeights()).thenReturn(articleWeightDTOs);

        mockMvc.perform(get("/articleWeights")
                        .content(mapper.writeValueAsString(articleWeightDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].article_weight_id").value(articleWeightDTO.getArticleWeightId()))
                .andExpect(jsonPath("$[0].article_id").value(articleWeightDTO.getArticleId()))
                .andExpect(jsonPath("$[0].weight").value(articleWeightDTO.getWeight()));

        System.out.println("Test testGetAllArticleWeights passed!");

    }

    @Test
    public void testGetArticleWeightByArticleId() throws Exception {

        when(articleWeightService.getArticleWeightByArticleId(any(int.class))).thenReturn(articleWeightDTO);

        mockMvc.perform(get("/articleWeights/article_id=1")
                        .content(mapper.writeValueAsString(articleWeightDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article_weight_id").value(articleWeightDTO.getArticleWeightId()))
                .andExpect(jsonPath("$.article_id").value(articleWeightDTO.getArticleId()))
                .andExpect(jsonPath("$.weight").value(articleWeightDTO.getWeight()));

        System.out.println("Test testGetArticleWeightByArticleId passed!");

    }

    @Test
    public void testCreateNewArticleWeight() throws Exception {

        when(articleWeightService.saveNewArticleWeight(any(ArticleWeightDTO.class))).thenReturn(articleWeightDTO);

        mockMvc.perform(post("/articleWeights/create")
                        .content(mapper.writeValueAsString(articleWeightDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.article_weight_id").value(articleWeightDTO.getArticleWeightId()))
                .andExpect(jsonPath("$.article_id").value(articleWeightDTO.getArticleId()))
                .andExpect(jsonPath("$.weight").value(articleWeightDTO.getWeight()));

        System.out.println("Test testCreateNewArticleWeight passed!");

    }

}
