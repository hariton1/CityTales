package group05.ase.openai.adapter.Controller;

import group05.ase.openai.adapter.Service.OpenAIService;
import group05.ase.openai.adapter.dto.EnrichmentRequest;
import group05.ase.openai.adapter.dto.EnrichmentResponse;
import group05.ase.openai.adapter.dto.SummaryRequest;
import group05.ase.openai.adapter.dto.SummaryResponse;
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

    @PostMapping("/summary")
    public ResponseEntity<SummaryResponse> generateSummary(@RequestBody SummaryRequest summaryRequest) {
        String content = summaryRequest.getContent();

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body( new SummaryResponse("<p>Missing input for summary.</p>"));
        }

        logger.info("POST /api/enrich/summary");
        SummaryResponse summary = openAIService.generateSummary(content);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/full")
    public ResponseEntity<EnrichmentResponse> enrichText(@RequestBody EnrichmentRequest enrichmentRequest) {
        String tone = enrichmentRequest.getTone();
        String content = enrichmentRequest.getContent();

        if (tone == null || content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new EnrichmentResponse("<p><em>Missing content or tone.</em></p>", tone != null ? tone : "tone unknown")
            );
        }

        logger.info("POST /api/enrich/full | Tone: {}", tone);

        EnrichmentResponse response = openAIService.generateEnrichedContent(tone, content);
        return ResponseEntity.ok(response);
    }
}
