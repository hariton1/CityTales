package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.UserBadgeDTO;
import group_05.ase.user_db.services.UserBadgeService;
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
public class UserBadgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    UserBadgeService userBadgeService;

    private final UserBadgeDTO userBadgeDTO = new UserBadgeDTO (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            10000,
            null
    );

    private final ArrayList<UserBadgeDTO> userBadgeDTOs = new ArrayList<>(List.of(userBadgeDTO));

    @Test
    public void testGetAllUserBadges() throws Exception {

        when(userBadgeService.getAllUserBadges()).thenReturn(userBadgeDTOs);

        mockMvc.perform(get("/userBadges")
                        .content(mapper.writeValueAsString(userBadgeDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_badge_id").value(userBadgeDTO.getUserBadgeId()))
                .andExpect(jsonPath("$[0].user_id").value(userBadgeDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].article_id").value(userBadgeDTO.getArticleId()));

    }

    @Test
    public void testGetUserBadgesByUserId() throws Exception {

        when(userBadgeService.getUserBadgesByUserId(any(UUID.class))).thenReturn(userBadgeDTOs);

        mockMvc.perform(get("/userBadges/user_id=f5599c8c-166b-495c-accc-65addfaa572b")
                        .content(mapper.writeValueAsString(userBadgeDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_badge_id").value(userBadgeDTO.getUserBadgeId()))
                .andExpect(jsonPath("$[0].user_id").value(userBadgeDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].article_id").value(userBadgeDTO.getArticleId()));

    }

    @Test
    public void testGetUserBadgesByArticleId() throws Exception {

        when(userBadgeService.getUserBadgesByArticleId(any(int.class))).thenReturn(userBadgeDTOs);

        mockMvc.perform(get("/userBadges/article_id=10000")
                        .content(mapper.writeValueAsString(userBadgeDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_badge_id").value(userBadgeDTO.getUserBadgeId()))
                .andExpect(jsonPath("$[0].user_id").value(userBadgeDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].article_id").value(userBadgeDTO.getArticleId()));

    }

    @Test
    public void testCreateNewBadge() throws Exception {

        when(userBadgeService.saveNewBadge(any(UserBadgeDTO.class))).thenReturn(userBadgeDTO);

        mockMvc.perform(post("/userBadges/create")
                        .content(mapper.writeValueAsString(userBadgeDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user_badge_id").value(userBadgeDTO.getUserBadgeId()))
                .andExpect(jsonPath("$.user_id").value(userBadgeDTO.getUserId().toString()))
                .andExpect(jsonPath("$.article_id").value(userBadgeDTO.getArticleId()));

    }
}
