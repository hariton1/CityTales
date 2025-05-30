package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.UserInterestDTO;
import group_05.ase.user_db.services.UserInterestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser; // hinzugefügt

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserInterestsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    UserInterestService userInterestService;

    private final UserInterestDTO userInterestDTO = new UserInterestDTO(
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            2,
            null,
            1
    );

    private final ArrayList<UserInterestDTO> userInterestDTOs = new ArrayList<>(List.of(userInterestDTO));

    @Test
    @WithMockUser(username = "testuser")
    public void testGetAllUserInterests() throws Exception {
        when(userInterestService.getAllUserInterests()).thenReturn(userInterestDTOs);

        mockMvc.perform(get("/userInterests")
                        .content(mapper.writeValueAsString(userInterestDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_id").value(userInterestDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].interest_id").value(userInterestDTO.getInterestId()))
                .andExpect(jsonPath("$[0].interest_weight").value(userInterestDTO.getInterestWeight()));

        System.out.println("Test testGetAllUserInterests passed!");
    }
    @Test
    @WithMockUser(username = "testuser")
    public void testGetUserInterestsByInterestId() throws Exception {
        when(userInterestService.getUserInterestsByInterestId(any(int.class))).thenReturn(userInterestDTOs);

        mockMvc.perform(get("/userInterests/interest_id=2")
                        .content(mapper.writeValueAsString(userInterestDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_id").value(userInterestDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].interest_id").value(userInterestDTO.getInterestId()))
                .andExpect(jsonPath("$[0].interest_weight").value(userInterestDTO.getInterestWeight()));

        System.out.println("Test testGetUserInterestsByInterestId passed!");
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreateNewUserInterest() throws Exception {
        System.out.println("Test testCreateNewUserInterest not provided!");
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testDeleteUserInterest() throws Exception {
        System.out.println("Test testDeleteUserInterest not provided!");
    }

    @Test
    void getOwnUserInterests_authenticated_shouldReturn200() throws Exception {
        String userId = UUID.randomUUID().toString();

        mockMvc.perform(get("/userInterests/me")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", userId)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnUserInterests_unauthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(get("/userInterests/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
