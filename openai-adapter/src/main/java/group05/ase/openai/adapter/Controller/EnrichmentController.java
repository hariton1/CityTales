package group05.ase.openai.adapter.Controller;

import group05.ase.openai.adapter.Service.OpenAIService;
import group05.ase.openai.adapter.dto.EnrichmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/enrich")
public class EnrichmentController {

    private static final Logger logger = LoggerFactory.getLogger(EnrichmentController.class);

    private final OpenAIService openAIService;

    public EnrichmentController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping
    public EnrichmentResponse enrichText(@RequestBody Map<String, String> body) {
        String tone = body.get("tone");
        String content = body.get("content");

        logger.info("POST /api/enrich");
        logger.info("Tone: {}", tone);
        return this.openAIService.generateResponse(tone, content);
    }
}
