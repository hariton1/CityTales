package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.ArticleWeightDTO;
import group_05.ase.user_db.services.ArticleWeightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articleWeights")
public class ArticleWeightController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleWeightController.class);

    private final ArticleWeightService articleWeightService;

    public ArticleWeightController(ArticleWeightService articleWeightService) {
        this.articleWeightService = articleWeightService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ArticleWeightDTO> getAllArticleWeights() {
        try {
            logger.info("Fetching all article weights");
            return this.articleWeightService.getAllArticleWeights();
        } catch (Exception e) {
            logger.error("Error fetching article weights: {}", e.getMessage());
            throw new RuntimeException("Error fetching article weights", e);
        }
    }

    @GetMapping("/article_id={articleId}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleWeightDTO getArticleWeightByArticleId(@PathVariable("articleId") int articleId) {
        try {
            logger.info("Fetching article weight by id: {}", articleId);
            return this.articleWeightService.getArticleWeightByArticleId(articleId);
        } catch (Exception e) {
            logger.error("Error fetching article weight by id {}: {}", articleId, e.getMessage());
            throw new RuntimeException("Error fetching article weight", e);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleWeightDTO createNewArticleWeight(@RequestBody ArticleWeightDTO articleWeightDTO) {
        try {
            logger.info("Creating article weight: {}", articleWeightDTO.toString());
            return this.articleWeightService.saveNewArticleWeight(articleWeightDTO);
        } catch (Exception e) {
            logger.error("Error when creating article weight {}: {}", articleWeightDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating article weight", e);
        }
    }

}
