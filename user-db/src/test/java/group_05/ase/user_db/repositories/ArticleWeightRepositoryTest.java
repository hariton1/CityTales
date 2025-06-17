package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.ArticleWeightEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest()
public class ArticleWeightRepositoryTest {

    @Autowired
    ArticleWeightRepository articleWeightRepository;

    private final ArticleWeightEntity articleWeightEntity = new ArticleWeightEntity(
            1,
            25471,
            0.01F
    );

    @Test
    public void testFindByArticleId() {

        /*ArticleWeightEntity tmp = articleWeightRepository.findByArticleId(this.articleWeightEntity.getArticleId());

        assertThat(tmp).isNotNull();
        assertThat(tmp.getArticleWeightId()).isEqualTo(this.articleWeightEntity.getArticleWeightId());
        assertThat(tmp.getArticleId()).isEqualTo(this.articleWeightEntity.getArticleId());
        assertThat(tmp.getWeight()).isEqualTo(this.articleWeightEntity.getWeight());*/

    }

    @Test
    public void testFindAllByOrderByWeightDesc() {

        /*ArrayList<ArticleWeightEntity> tmp = new ArrayList<>(articleWeightRepository.findAllByOrderByWeightDesc());

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getArticleWeightId()).isEqualTo(articleWeightEntity.getArticleWeightId());
        assertThat(tmp.getFirst().getArticleId()).isEqualTo(articleWeightEntity.getArticleId());
        assertThat(tmp.getFirst().getWeight()).isEqualTo(articleWeightEntity.getWeight());*/

    }

}
