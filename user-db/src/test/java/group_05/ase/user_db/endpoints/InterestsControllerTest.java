package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.InterestDTO;
import group_05.ase.user_db.services.InterestService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InterestsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    InterestService interestService;

    private final InterestDTO interestDTO = new InterestDTO (
            2,
            4,
            "classical",
            "the person is interested in classical music"
    );

    private final ArrayList<InterestDTO> interestDTOs = new ArrayList<>(List.of(interestDTO));

    @Test
    public void testGetAllInterests() throws Exception {

        when(interestService.getAllInterests()).thenReturn(interestDTOs);

        mockMvc.perform(get("/interests")
                        .content(mapper.writeValueAsString(interestDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].interest_id").value(interestDTO.getInterestId()))
                .andExpect(jsonPath("$[0].interest_type_id").value(interestDTO.getInterestTypeId()))
                .andExpect(jsonPath("$[0].interest_name").value(interestDTO.getInterestName()))
                .andExpect(jsonPath("$[0].description").value(interestDTO.getDescription()));

        System.out.println("Test testGetAllInterests passed!");

    }

    @Test
    public void testGetInterestByInterestId() throws Exception {

        when(interestService.getInterestById(any(int.class))).thenReturn(interestDTO);

        mockMvc.perform(get("/interests/id=1")
                        .content(mapper.writeValueAsString(interestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interest_id").value(interestDTO.getInterestId()))
                .andExpect(jsonPath("$.interest_type_id").value(interestDTO.getInterestTypeId()))
                .andExpect(jsonPath("$.interest_name").value(interestDTO.getInterestName()))
                .andExpect(jsonPath("$.description").value(interestDTO.getDescription()));

        System.out.println("Test testGetInterestByInterestId passed!");

    }

    @Test
    public void testGetInterestByInterestTypeId() throws Exception {

        when(interestService.getInterestByInterestTypeId(any(int.class))).thenReturn(interestDTO);

        mockMvc.perform(get("/interests/type_id=4")
                        .content(mapper.writeValueAsString(interestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interest_id").value(interestDTO.getInterestId()))
                .andExpect(jsonPath("$.interest_type_id").value(interestDTO.getInterestTypeId()))
                .andExpect(jsonPath("$.interest_name").value(interestDTO.getInterestName()))
                .andExpect(jsonPath("$.description").value(interestDTO.getDescription()));

        System.out.println("Test testGetInterestByInterestTypeId passed!");

    }

    @Test
    public void testGetInterestByName() throws Exception {

        when(interestService.getInterestByName(any(String.class))).thenReturn(interestDTO);

        mockMvc.perform(get("/interests/name=classical")
                        .content(mapper.writeValueAsString(interestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interest_id").value(interestDTO.getInterestId()))
                .andExpect(jsonPath("$.interest_type_id").value(interestDTO.getInterestTypeId()))
                .andExpect(jsonPath("$.interest_name").value(interestDTO.getInterestName()))
                .andExpect(jsonPath("$.description").value(interestDTO.getDescription()));

        System.out.println("Test testGetInterestByName passed!");

    }

    @Test
    public void testGetInterestsByDescriptionLike() throws Exception {

        when(interestService.getInterestsByDescriptionLike(any(String.class))).thenReturn(interestDTOs);

        mockMvc.perform(get("/interests/description=music")
                        .content(mapper.writeValueAsString(interestDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].interest_id").value(interestDTO.getInterestId()))
                .andExpect(jsonPath("$[0].interest_type_id").value(interestDTO.getInterestTypeId()))
                .andExpect(jsonPath("$[0].interest_name").value(interestDTO.getInterestName()))
                .andExpect(jsonPath("$[0].description").value(interestDTO.getDescription()));

        System.out.println("Test testGetInterestsByDescriptionLike passed!");

    }

    @Test
    public void testCreateNewInterest() throws Exception {

        System.out.println("Test testCreateNewInterest not provided!");

    }

}
