package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.QuizDTO;
import group_05.ase.user_db.restData.QuizResultDTO;
import group_05.ase.user_db.services.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    private final QuizService service;

    public QuizController(QuizService service) {
        this.service = service;
    }

    @GetMapping("/quiz/user")
    @ResponseStatus(HttpStatus.OK)
    public List<QuizDTO> getQuizzesForUser(@RequestBody UUID user) {
        try {
            return service.getQuizzesForUser(user);
        } catch (Exception e) {
            logger.error("Error fetching quiz for user {}: {}", user, e.getMessage());
            throw new RuntimeException("Error fetching quiz", e);
        }
    }

    @GetMapping("/result/user")
    @ResponseStatus(HttpStatus.OK)
    public List<QuizResultDTO> getResultsForUser(@RequestBody UUID user) {
        try {
            return service.getResultsForUser(user);
        } catch (Exception e) {
            logger.error("Error fetching quiz results for user {}: {}", user, e.getMessage());
            throw new RuntimeException("Error fetching quiz results", e);
        }
    }

    @PostMapping("/quiz/create")
    @ResponseStatus(HttpStatus.OK)
    public QuizDTO saveQuiz(@RequestBody QuizDTO quiz) {
        try {
            return service.saveQuiz(quiz);
        } catch (Exception e) {
            logger.error("Error saving quiz {}: {}", quiz, e.getMessage());
            throw new RuntimeException("Error saving quiz", e);
        }
    }
}
