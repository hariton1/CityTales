package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.QuizDTO;
import group_05.ase.user_db.restData.QuizResultDTO;
import group_05.ase.user_db.restData.QuizUserDTO;
import group_05.ase.user_db.services.QuizService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    private final QuizService service;

    public QuizController(QuizService service) {
        this.service = service;
    }

    @GetMapping("/quiz/user={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<QuizDTO> getQuizzesForUser(@PathVariable UUID userId) {
        try {
            return service.getQuizzesForUser(userId);
        } catch (Exception e) {
            logger.error("Error fetching quiz for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching quiz", e);
        }
    }

    @GetMapping("/result/user={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<QuizResultDTO> getResultsForUser(@PathVariable UUID userId) {
        try {
            return service.getResultsForUser(userId);
        } catch (Exception e) {
            logger.error("Error fetching quiz results for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching quiz results", e);
        }
    }

    @PostMapping("/quiz/create/{category}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<QuizDTO> saveQuiz(@PathVariable("category") String category, @RequestBody List<UUID> users) {
        try {
            QuizDTO response = service.saveQuiz(category, users);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error creating quiz for {}: {}", users, e.getMessage());
            throw new RuntimeException("Error saving quiz", e);
        }
    }

    @PostMapping("/result/save")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<QuizResultDTO> saveQuestionResults(@RequestBody QuizResultDTO dto) {
        try {
            QuizResultDTO response = service.saveQuestionResults(dto);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error creating quiz result for {}: {}", dto, e.getMessage());
            throw new RuntimeException("Error saving quiz result", e);
        }
    }

    @PostMapping("/quiz/invite")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<QuizUserDTO>> saveQuizForUsers(@RequestBody List<QuizUserDTO> dtoList) {
        try {
            List<QuizUserDTO> persistedQuizUserDtoList = service.saveQuizForUsers(dtoList);
            return ResponseEntity.ok(persistedQuizUserDtoList);
        } catch (Exception e) {
            logger.error("Error saving quiz for users {}: {}", dtoList, e.getMessage());
            throw new RuntimeException("Error fetching quiz", e);
        }
    }
}
