package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.SavedFunFactDTO;
import group_05.ase.user_db.services.SavedFunFactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser
@AutoConfigureMockMvc(addFilters = false)
public class SavedFunFactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    SavedFunFactService savedFunFactService;

    private final SavedFunFactDTO savedFunFactDTO = new SavedFunFactDTO (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            1,
            "test headline",
            "test funFact",
            "test imageUrl",
            0.01F,
            "test reason",
            null
    );

    private final ArrayList<SavedFunFactDTO> savedFunFactDTOs = new ArrayList<>(List.of(savedFunFactDTO));

    @Test
    public void testGetAllByUserId() throws Exception {
        when(savedFunFactService.getAllByUserId(any(UUID.class))).thenReturn(savedFunFactDTOs);

        mockMvc.perform(get("/funFacts/user_id=f5599c8c-166b-495c-accc-65addfaa572b")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].saved_fun_fact_id").value(savedFunFactDTO.getSavedFunFactId()))
                .andExpect(jsonPath("$[0].user_id").value(savedFunFactDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].article_id").value(savedFunFactDTO.getArticleId()))
                .andExpect(jsonPath("$[0].headline").value(savedFunFactDTO.getHeadline()))
                .andExpect(jsonPath("$[0].fun_fact").value(savedFunFactDTO.getFunFact()))
                .andExpect(jsonPath("$[0].image_url").value(savedFunFactDTO.getImageUrl()))
                .andExpect(jsonPath("$[0].score").value(savedFunFactDTO.getScore()))
                .andExpect(jsonPath("$[0].reason").value(savedFunFactDTO.getReason()));
    }

    @Test
    public void testCreateNewSavedFunFact() throws Exception {
        when(savedFunFactService.saveNewSavedFunFact(any(SavedFunFactDTO.class))).thenReturn(savedFunFactDTO);

        mockMvc.perform(post("/funFacts/create")
                        .content(mapper.writeValueAsString(savedFunFactDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saved_fun_fact_id").value(savedFunFactDTO.getSavedFunFactId()))
                .andExpect(jsonPath("$.user_id").value(savedFunFactDTO.getUserId().toString()))
                .andExpect(jsonPath("$.article_id").value(savedFunFactDTO.getArticleId()))
                .andExpect(jsonPath("$.headline").value(savedFunFactDTO.getHeadline()))
                .andExpect(jsonPath("$.fun_fact").value(savedFunFactDTO.getFunFact()))
                .andExpect(jsonPath("$.image_url").value(savedFunFactDTO.getImageUrl()))
                .andExpect(jsonPath("$.score").value(savedFunFactDTO.getScore()))
                .andExpect(jsonPath("$.reason").value(savedFunFactDTO.getReason()));
    }

}
