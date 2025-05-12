package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.UserHistoryDTO;
import group_05.ase.user_db.services.UserHistoryService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserHistoriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    UserHistoryService userHistoryService;

    private final UserHistoryDTO userHistoryDTO = new UserHistoryDTO (
            2,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572a"),
            null,
            null,
            2
    );

    private final ArrayList<UserHistoryDTO> userHistoryDTOs = new ArrayList<>(List.of(userHistoryDTO));

    @Test
    public void testGetAllUserHistories() throws Exception {

        when(userHistoryService.getAllUserHistories()).thenReturn(userHistoryDTOs);

        mockMvc.perform(get("/userHistories")
                        .content(mapper.writeValueAsString(userHistoryDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_history_id").value(userHistoryDTO.getUserHistoryId()))
                .andExpect(jsonPath("$[0].user_id").value(userHistoryDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].article_id").value(userHistoryDTO.getArticleId().toString()))
                .andExpect(jsonPath("$[0].interest_id").value(userHistoryDTO.getInterestId()));

        System.out.println("Test testGetAllUserHistories passed!");

    }

    @Test
    public void testGetUserHistoriesById() throws Exception {

        when(userHistoryService.getUserHistoriesById(any(int.class))).thenReturn(userHistoryDTO);

        mockMvc.perform(get("/userHistories/id=2")
                        .content(mapper.writeValueAsString(userHistoryDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_history_id").value(userHistoryDTO.getUserHistoryId()))
                .andExpect(jsonPath("$.user_id").value(userHistoryDTO.getUserId().toString()))
                .andExpect(jsonPath("$.article_id").value(userHistoryDTO.getArticleId().toString()))
                .andExpect(jsonPath("$.interest_id").value(userHistoryDTO.getInterestId()));

        System.out.println("Test testGetUserHistoriesById passed!");

    }

    @Test
    public void testGetUserHistoriesByUserId() throws Exception {

        when(userHistoryService.getUserHistoriesByUserId(any(UUID.class))).thenReturn(userHistoryDTOs);

        mockMvc.perform(get("/userHistories/user_id=f5599c8c-166b-495c-accc-65addfaa572b")
                        .content(mapper.writeValueAsString(userHistoryDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_history_id").value(userHistoryDTO.getUserHistoryId()))
                .andExpect(jsonPath("$[0].user_id").value(userHistoryDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].article_id").value(userHistoryDTO.getArticleId().toString()))
                .andExpect(jsonPath("$[0].interest_id").value(userHistoryDTO.getInterestId()));

        System.out.println("Test testGetUserHistoriesByUserId passed!");

    }

    @Test
    public void testGetUserHistoriesByArticleId() throws Exception {

        when(userHistoryService.getUserHistoriesByArticleId(any(UUID.class))).thenReturn(userHistoryDTOs);

        mockMvc.perform(get("/userHistories/article_id=f5599c8c-166b-495c-accc-65addfaa572a")
                        .content(mapper.writeValueAsString(userHistoryDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_history_id").value(userHistoryDTO.getUserHistoryId()))
                .andExpect(jsonPath("$[0].user_id").value(userHistoryDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].article_id").value(userHistoryDTO.getArticleId().toString()))
                .andExpect(jsonPath("$[0].interest_id").value(userHistoryDTO.getInterestId()));

        System.out.println("Test testGetUserHistoriesByArticleId passed!");

    }

    @Test
    public void testCreateNewUserHistory() throws Exception {

        System.out.println("Test testCreateNewUserHistory not provided!");

    }

    @Test
    public void testUpdateUserHistory() throws Exception {

        System.out.println("Test testUpdateUserHistory not provided!");

    }

}
