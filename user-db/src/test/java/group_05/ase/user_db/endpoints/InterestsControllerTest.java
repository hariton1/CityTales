package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.InterestDTO;
import group_05.ase.user_db.services.InterestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class InterestsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterestService interestService;

    @Autowired
    private ObjectMapper objectMapper;

    private final InterestDTO interestDTO = new InterestDTO(
            2,
            "music",
            "the person is interested in music",
            "Musik"
    );

    @Test
    @DisplayName("GET /interests returns all interests")
    @WithMockUser
    void shouldReturnAllInterests() throws Exception {
        when(interestService.getAllInterests()).thenReturn(List.of(interestDTO));

        mockMvc.perform(get("/interests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].interest_id").value(interestDTO.getInterestId()))
                .andExpect(jsonPath("$[0].interest_name").value(interestDTO.getInterestNameEn()))
                .andExpect(jsonPath("$[0].interest_name_de").value(interestDTO.getInterestNameDe()))
                .andExpect(jsonPath("$[0].description").value(interestDTO.getDescription()));
    }

    @Test
    @DisplayName("GET /interests/id={id} returns interest by ID")
    @WithMockUser
    void shouldReturnInterestById() throws Exception {
        when(interestService.getInterestById(any(Integer.class))).thenReturn(interestDTO);

        mockMvc.perform(get("/interests/id=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interest_id").value(interestDTO.getInterestId()))
                .andExpect(jsonPath("$.interest_name").value(interestDTO.getInterestNameEn()))
                .andExpect(jsonPath("$.interest_name_de").value(interestDTO.getInterestNameDe()))
                .andExpect(jsonPath("$.description").value(interestDTO.getDescription()));
    }

    @Test
    @DisplayName("GET /interests/name={name} returns interest by English name")
    @WithMockUser
    void shouldReturnInterestByNameEn() throws Exception {
        when(interestService.getInterestByNameEn(any(String.class))).thenReturn(interestDTO);

        mockMvc.perform(get("/interests/name=music")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interest_id").value(interestDTO.getInterestId()))
                .andExpect(jsonPath("$.interest_name").value(interestDTO.getInterestNameEn()))
                .andExpect(jsonPath("$.interest_name_de").value(interestDTO.getInterestNameDe()))
                .andExpect(jsonPath("$.description").value(interestDTO.getDescription()));
    }

    @Test
    @DisplayName("GET /interests/de/name={nameDe} returns interest by German name")
    @WithMockUser
    void shouldReturnInterestByNameDe() throws Exception {
        when(interestService.getInterestByNameDe(any(String.class))).thenReturn(interestDTO);

        mockMvc.perform(get("/interests/de/name=Musik")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interest_id").value(interestDTO.getInterestId()))
                .andExpect(jsonPath("$.interest_name").value(interestDTO.getInterestNameEn()))
                .andExpect(jsonPath("$.interest_name_de").value(interestDTO.getInterestNameDe()))
                .andExpect(jsonPath("$.description").value(interestDTO.getDescription()));
    }

    @Test
    @DisplayName("GET /interests/description={desc} returns interests by description")
    @WithMockUser
    void shouldReturnInterestsByDescriptionLike() throws Exception {
        when(interestService.getInterestsByDescriptionLike(any(String.class))).thenReturn(List.of(interestDTO));

        mockMvc.perform(get("/interests/description=music")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].interest_id").value(interestDTO.getInterestId()))
                .andExpect(jsonPath("$[0].interest_name").value(interestDTO.getInterestNameEn()))
                .andExpect(jsonPath("$[0].interest_name_de").value(interestDTO.getInterestNameDe()))
                .andExpect(jsonPath("$[0].description").value(interestDTO.getDescription()));
    }

    @Test
    @DisplayName("POST /interests/create creates a new interest (void return)")
    @WithMockUser
    void shouldCreateNewInterest() throws Exception {
        doNothing().when(interestService).saveNewInterest(any(InterestDTO.class));

        mockMvc.perform(post("/interests/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(interestDTO)))
                .andExpect(status().isCreated());
    }

}
