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
public class FeedbacksControllerTest {

	@Autowired
	private MockMvc mockMvc;

	ObjectMapper mapper = new ObjectMapper();

	@MockBean
	FeedbackService feedbackService;

	private final FeedbackDTO feedbackDTO = new FeedbackDTO (
			1,
			UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
			1,
			100.0,
			"Completely correct",
			null
	);

	private final ArrayList<FeedbackDTO> feedbackDTOs = new ArrayList<>(List.of(feedbackDTO));

	@Test
	public void testGetAllFeedbacks() throws Exception {

		when(feedbackService.getAllFeedbacks()).thenReturn(feedbackDTOs);

		mockMvc.perform(get("/feedbacks")
						.content(mapper.writeValueAsString(feedbackDTOs))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].feedback_id").value(feedbackDTO.getFeedbackId()))
				.andExpect(jsonPath("$[0].user_id").value(feedbackDTO.getUserId().toString()))
				.andExpect(jsonPath("$[0].article_id").value(feedbackDTO.getArticleId()))
				.andExpect(jsonPath("$[0].rating").value(feedbackDTO.getRating()))
				.andExpect(jsonPath("$[0].fb_content").value(feedbackDTO.getFbContent()));

		System.out.println("Test testGetAllFeedbacks passed!");

	}

	@Test
	public void testGetFeedbackById() throws Exception {

		when(feedbackService.getFeedbackById(any(int.class))).thenReturn(feedbackDTO);

		mockMvc.perform(get("/feedbacks/id=1")
						.content(mapper.writeValueAsString(feedbackDTO))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.feedback_id").value(feedbackDTO.getFeedbackId()));

		System.out.println("Test testGetFeedbackById passed!");

	}

	@Test
	public void testGetFeedbacksByUserId() throws Exception {

		when(feedbackService.getFeedbacksByUserId(any(UUID.class))).thenReturn(feedbackDTOs);

		mockMvc.perform(get("/feedbacks/user_id=f5599c8c-166b-495c-accc-65addfaa572b")
						.content(mapper.writeValueAsString(feedbackDTOs))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].feedback_id").value(feedbackDTO.getFeedbackId()))
				.andExpect(jsonPath("$[0].user_id").value(feedbackDTO.getUserId().toString()))
				.andExpect(jsonPath("$[0].article_id").value(feedbackDTO.getArticleId()))
				.andExpect(jsonPath("$[0].rating").value(feedbackDTO.getRating()))
				.andExpect(jsonPath("$[0].fb_content").value(feedbackDTO.getFbContent()));

		System.out.println("Test testGetFeedbacksByUserId passed!");

	}

	@Test
	public void testGetFeedbacksByArticleId() throws Exception {

		when(feedbackService.getFeedbacksByArticleId(any(int.class))).thenReturn(feedbackDTOs);

		mockMvc.perform(get("/feedbacks/article_id=1")
						.content(mapper.writeValueAsString(feedbackDTOs))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].feedback_id").value(feedbackDTO.getFeedbackId()))
				.andExpect(jsonPath("$[0].user_id").value(feedbackDTO.getUserId().toString()))
				.andExpect(jsonPath("$[0].article_id").value(feedbackDTO.getArticleId()))
				.andExpect(jsonPath("$[0].rating").value(feedbackDTO.getRating()))
				.andExpect(jsonPath("$[0].fb_content").value(feedbackDTO.getFbContent()));

		System.out.println("Test testGetFeedbacksByArticleId passed!");

	}

	@Test
	public void testGetFeedbacksByFbContentLike() throws Exception {

		when(feedbackService.getFeedbacksByFbContentLike(any(String.class))).thenReturn(feedbackDTOs);

		mockMvc.perform(get("/feedbacks/content=corr")
						.content(mapper.writeValueAsString(feedbackDTOs))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].feedback_id").value(feedbackDTO.getFeedbackId()))
				.andExpect(jsonPath("$[0].user_id").value(feedbackDTO.getUserId().toString()))
				.andExpect(jsonPath("$[0].article_id").value(feedbackDTO.getArticleId()))
				.andExpect(jsonPath("$[0].rating").value(feedbackDTO.getRating()))
				.andExpect(jsonPath("$[0].fb_content").value(feedbackDTO.getFbContent()));

		System.out.println("Test testGetFeedbacksByFbContentLike passed!");

	}

	@Test
	public void testCreateNewFeedback() throws Exception {

		System.out.println("Test testCreateNewFeedback not provided!");

	}

}
