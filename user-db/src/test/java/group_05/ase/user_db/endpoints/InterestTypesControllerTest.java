package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.InterestTypeDTO;
import group_05.ase.user_db.services.InterestTypeService;
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
public class InterestTypesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    InterestTypeService interestTypeService;

    private final InterestTypeDTO interestTypeDTO = new InterestTypeDTO (
            1,
            "medic",
            "the person is interested in medical fun-facts",
            null
    );

    private final ArrayList<InterestTypeDTO> interestTypeDTOs = new ArrayList<>(List.of(interestTypeDTO));

    @Test
    public void testGetAllInterests() throws Exception {

        when(interestTypeService.getAllInterestTypes()).thenReturn(interestTypeDTOs);

        mockMvc.perform(get("/interestTypes")
                        .content(mapper.writeValueAsString(interestTypeDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].interest_type_id").value(interestTypeDTO.getInterestTypeId()))
                .andExpect(jsonPath("$[0].type_name").value(interestTypeDTO.getTypeName()))
                .andExpect(jsonPath("$[0].description").value(interestTypeDTO.getDescription()));

        System.out.println("Test testGetAllInterests passed!");

    }

    @Test
    public void testGetInterestTypesByInterestTypeId() throws Exception {

        when(interestTypeService.getInterestById(any(int.class))).thenReturn(interestTypeDTO);

        mockMvc.perform(get("/interestTypes/id=1")
                        .content(mapper.writeValueAsString(interestTypeDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interest_type_id").value(interestTypeDTO.getInterestTypeId()))
                .andExpect(jsonPath("$.type_name").value(interestTypeDTO.getTypeName()))
                .andExpect(jsonPath("$.description").value(interestTypeDTO.getDescription()));

        System.out.println("Test testGetInterestTypesByInterestTypeId passed!");

    }

    @Test
    public void testGetInterestTypesByTypeName() throws Exception {

        when(interestTypeService.getInterestByName(any(String.class))).thenReturn(interestTypeDTO);

        mockMvc.perform(get("/interestTypes/name=medic")
                        .content(mapper.writeValueAsString(interestTypeDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interest_type_id").value(interestTypeDTO.getInterestTypeId()))
                .andExpect(jsonPath("$.type_name").value(interestTypeDTO.getTypeName()))
                .andExpect(jsonPath("$.description").value(interestTypeDTO.getDescription()));

        System.out.println("Test testGetInterestTypesByTypeName passed!");

    }

    @Test
    public void testGetInterestTypesByDescriptionLike() throws Exception {

        when(interestTypeService.getInterestTypesByDescriptionLike(any(String.class))).thenReturn(interestTypeDTOs);

        mockMvc.perform(get("/interestTypes/description=fun-facts")
                        .content(mapper.writeValueAsString(interestTypeDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].interest_type_id").value(interestTypeDTO.getInterestTypeId()))
                .andExpect(jsonPath("$[0].type_name").value(interestTypeDTO.getTypeName()))
                .andExpect(jsonPath("$[0].description").value(interestTypeDTO.getDescription()));

        System.out.println("Test testGetInterestTypesByDescriptionLike passed!");

    }

    @Test
    public void testCreateNewInterestType() throws Exception {

        System.out.println("Test testCreateNewInterestType not provided!");

    }

}
