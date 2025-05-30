package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.UserPointDTO;
import group_05.ase.user_db.services.UserPointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser; // <--- Import hinzugefügt

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserPointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    UserPointService userPointService;

    private final UserPointDTO userPointDTO = new UserPointDTO(
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            1,
            null
    );

    private final ArrayList<UserPointDTO> userPointDTOs = new ArrayList<>(List.of(userPointDTO));

    @Test
    @WithMockUser(username = "testuser")
    public void testGetAllUserPoints() throws Exception {
        when(userPointService.getAllUserPoints()).thenReturn(userPointDTOs);

        mockMvc.perform(get("/userPoints")
                        .content(mapper.writeValueAsString(userPointDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_point_id").value(userPointDTO.getUserPointId()))
                .andExpect(jsonPath("$[0].user_id").value(userPointDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].points").value(userPointDTO.getPoints()));

        System.out.println("Test testGetAllUserPoints passed!");
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetUserPointById() throws Exception {
        when(userPointService.getUserPointById(any(int.class))).thenReturn(userPointDTO);

        mockMvc.perform(get("/userPoints/id=1")
                        .content(mapper.writeValueAsString(userPointDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_point_id").value(userPointDTO.getUserPointId()))
                .andExpect(jsonPath("$.user_id").value(userPointDTO.getUserId().toString()))
                .andExpect(jsonPath("$.points").value(userPointDTO.getPoints()));

        System.out.println("Test testGetUserPointById passed!");
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetUserPointsByUserId() throws Exception {
        when(userPointService.getUserPointsByUserId(any(UUID.class))).thenReturn(userPointDTOs);

        mockMvc.perform(get("/userPoints/user_id=f5599c8c-166b-495c-accc-65addfaa572b")
                        .content(mapper.writeValueAsString(userPointDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_point_id").value(userPointDTO.getUserPointId()))
                .andExpect(jsonPath("$[0].user_id").value(userPointDTO.getUserId().toString()))
                .andExpect(jsonPath("$[0].points").value(userPointDTO.getPoints()));

        System.out.println("Test testGetUserPointsByUserId passed!");
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreateNewPoints() throws Exception {
        System.out.println("Test testCreateNewPoints not provided!");
    }
}
