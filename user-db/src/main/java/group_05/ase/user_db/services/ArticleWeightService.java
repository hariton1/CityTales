package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.ArticleWeightEntity;
import group_05.ase.user_db.repositories.ArticleWeightRepository;
import group_05.ase.user_db.restData.ArticleWeightDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleWeightService {

    private final ArticleWeightRepository repository;

    public ArticleWeightService(ArticleWeightRepository repository) {
        this.repository = repository;
    }

    public List<ArticleWeightDTO> getAllArticleWeights() {

        ArrayList<ArticleWeightDTO> articleWeights = new ArrayList<>();
        List<ArticleWeightEntity> tmp = this.repository.findAllByOrderByWeightDesc();

        for(ArticleWeightEntity articleWeight : tmp) {
            articleWeights.add(new ArticleWeightDTO(
                    articleWeight.getArticleWeightId(),
                    articleWeight.getArticleId(),
                    articleWeight.getWeight()
            ));
        }

        return articleWeights;

    }

    public ArticleWeightDTO getArticleWeightByArticleId(int articleId) {

        ArticleWeightEntity tmp = this.repository.findByArticleId(articleId);

        return new ArticleWeightDTO(tmp.getArticleWeightId(), tmp.getArticleId(), tmp.getWeight());

    }

    public ArticleWeightDTO saveNewArticleWeight(ArticleWeightDTO articleWeightDTO) {

        ArticleWeightEntity tmp = new ArticleWeightEntity();

        tmp.setArticleId(articleWeightDTO.getArticleId());
        tmp.setWeight(articleWeightDTO.getWeight());

        ArticleWeightEntity insertedArticleWeight = this.repository.save(tmp);

        return new ArticleWeightDTO(
                insertedArticleWeight.getArticleWeightId(),
                insertedArticleWeight.getArticleId(),
                insertedArticleWeight.getWeight()
        );

    }

}
