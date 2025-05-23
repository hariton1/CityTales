package group05.ase.openai.adapter.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/enrich")
public class EnrichmentController {

    private static final Logger logger = LoggerFactory.getLogger(EnrichmentController.class);

    @GetMapping
    public ResponseEntity<String> testController() {
        logger.info("GET /api/enrich");
        return ResponseEntity.ok("Testing...");
    }
}
