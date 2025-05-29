package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.ArticleWeightDTO;
import group_05.ase.user_db.services.ArticleWeightService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/articleWeights")
public class ArticleWeightController {

    private final ArticleWeightService articleWeightService;

    public ArticleWeightController(ArticleWeightService articleWeightService) {
        this.articleWeightService = articleWeightService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ArticleWeightDTO> getAllArticleWeights() {
        try {
            return this.articleWeightService.getAllArticleWeights();
        } catch (Exception e) {
            return new ArrayList<ArticleWeightDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/article_id={articleId}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleWeightDTO getArticleWeightByArticleId(@PathVariable("articleId") int articleId) {
        try {
            return this.articleWeightService.getArticleWeightByArticleId(articleId);
        } catch (Exception e) {
            return new ArticleWeightDTO(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleWeightDTO createNewArticleWeight(@RequestBody ArticleWeightDTO articleWeightDTO) {
        try {
            return this.articleWeightService.saveNewArticleWeight(articleWeightDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArticleWeightDTO();
        }
    }

}
