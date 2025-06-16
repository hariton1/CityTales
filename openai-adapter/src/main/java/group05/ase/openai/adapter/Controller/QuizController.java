package group05.ase.openai.adapter.Controller;

import group05.ase.openai.adapter.Service.OpenAIService;
import group05.ase.openai.adapter.dto.QuizAdditionalResponse;
import group05.ase.openai.adapter.dto.QuizQuestionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    private final OpenAIService openAIService;

    public QuizController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/generate")
    public QuizQuestionResponse generateQuiz(@RequestBody String prompt) {
        logger.info("Generate quiz");
        return this.openAIService.generateQuiz(prompt);
    }

    @PostMapping("/generate_additional")
    public QuizAdditionalResponse generateAdditionalQuizData(@RequestBody String prompt) {
        logger.info("Generate quiz additional data");
        return this.openAIService.generateAdditionalQuizData(prompt);
    }
}
