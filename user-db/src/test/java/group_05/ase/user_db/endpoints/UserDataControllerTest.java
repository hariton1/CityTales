package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.UserDataDTO;
import group_05.ase.user_db.services.UserDataService;
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
public class UserDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    UserDataService userDataService;

    private final UserDataDTO userDataDto = new UserDataDTO (
            -1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            "Admin",
            "Active",
            null
    );

    private final ArrayList<UserDataDTO> userDataDTOs = new ArrayList<>(List.of(userDataDto));

    @Test
    public void testGetAllUserData() throws Exception {
        when(userDataService.getAllUserData()).thenReturn(userDataDTOs);

        mockMvc.perform(get("/userData")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_data_id").value(userDataDto.getUserDataId()))
                .andExpect(jsonPath("$[0].user_id").value(userDataDto.getUserId().toString()))
                .andExpect(jsonPath("$[0].role_name").value(userDataDto.getRoleName()))
                .andExpect(jsonPath("$[0].status").value(userDataDto.getStatus()));

        System.out.println("Test testGetAllUserData passed!");
    }

    @Test
    public void testGetUserDataById() throws Exception {
        when(userDataService.getUserDataById(any(int.class))).thenReturn(userDataDto);

        mockMvc.perform(get("/userData/id=-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data_id").value(userDataDto.getUserDataId()))
                .andExpect(jsonPath("$.user_id").value(userDataDto.getUserId().toString()))
                .andExpect(jsonPath("$.role_name").value(userDataDto.getRoleName()))
                .andExpect(jsonPath("$.status").value(userDataDto.getStatus()));

        System.out.println("Test testGetUserDataById passed!");
    }

    @Test
    public void testGetUserDataByUserId() throws Exception {
        when(userDataService.getUserDataByUserId(any(UUID.class))).thenReturn(userDataDto);

        mockMvc.perform(get("/userData/user_id=f5599c8c-166b-495c-accc-65addfaa572b")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data_id").value(userDataDto.getUserDataId()))
                .andExpect(jsonPath("$.user_id").value(userDataDto.getUserId().toString()))
                .andExpect(jsonPath("$.role_name").value(userDataDto.getRoleName()))
                .andExpect(jsonPath("$.status").value(userDataDto.getStatus()));

        System.out.println("Test testGetUserDataByUserId passed!");
    }

    @Test
    public void testSaveUserData() throws Exception {
        when(userDataService.saveUserData(any(UserDataDTO.class))).thenReturn(userDataDto);

        mockMvc.perform(post("/userData/save")
                        .content(mapper.writeValueAsString(userDataDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user_data_id").value(userDataDto.getUserDataId()))
                .andExpect(jsonPath("$.user_id").value(userDataDto.getUserId().toString()))
                .andExpect(jsonPath("$.role_name").value(userDataDto.getRoleName()))
                .andExpect(jsonPath("$.status").value(userDataDto.getStatus()));

        System.out.println("Test testSaveUserData passed!");
    }

}
