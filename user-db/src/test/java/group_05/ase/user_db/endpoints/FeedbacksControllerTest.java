package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.FeedbackDTO;
import group_05.ase.user_db.services.FeedbackService;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FeedbacksControllerTest {

	@Autowired
	private MockMvc mockMvc;

	ObjectMapper mapper = new ObjectMapper();

	@MockBean
	FeedbackService feedbackService;

	@Test
	public void testGetFeedbackById() throws Exception {

		FeedbackDTO feedbackDTO = new FeedbackDTO();
		feedbackDTO.setFeedbackId(1);

		when(feedbackService.getFeedbackById(any(int.class))).thenReturn(feedbackDTO);

		mockMvc.perform(get("/feedbacks/id=1")
				.content(mapper.writeValueAsString(feedbackDTO))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.feedback_id").value(feedbackDTO.getFeedbackId()));

		System.out.println(feedbackDTO.toString());

	}

}
