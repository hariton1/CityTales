package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.ArticleWeightEntity;
import group_05.ase.user_db.repositories.ArticleWeightRepository;
import group_05.ase.user_db.restData.ArticleWeightDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArticleWeightServiceTest {

    @Mock
    ArticleWeightRepository articleWeightRepository;

    @InjectMocks
    ArticleWeightService articleWeightService;

    private final ArticleWeightEntity articleWeightEntity = new ArticleWeightEntity (
            1,
            10000,
            0.01F
    );

    private final ArticleWeightDTO articleWeightDTO = new ArticleWeightDTO (
            1,
            10000,
            0.01F
    );

    private final ArrayList<ArticleWeightEntity> articleWeightEntities = new ArrayList<>(List.of(articleWeightEntity));

    @Test
    public void testGetAllArticleWeights() {

        when(articleWeightRepository.findAllByOrderByWeightDesc()).thenReturn(articleWeightEntities);

        ArrayList<ArticleWeightDTO> articleWeightDTOs = new ArrayList<>(articleWeightService.getAllArticleWeights());

        assertThat(articleWeightDTOs.getFirst().getArticleWeightId()).isEqualTo(articleWeightEntity.getArticleWeightId());
        assertThat(articleWeightDTOs.getFirst().getArticleId()).isEqualTo(articleWeightEntity.getArticleId());
        assertThat(articleWeightDTOs.getFirst().getWeight()).isEqualTo(articleWeightEntity.getWeight());

        System.out.println("Test testGetAllArticleWeights passed!");

    }

    @Test
    public void testGetArticleWeightByArticleId() {

        when(articleWeightRepository.findByArticleId(any(int.class))).thenReturn(articleWeightEntity);

        ArticleWeightDTO articleWeightDTO = articleWeightService.getArticleWeightByArticleId(articleWeightEntity.getArticleId());

        assertThat(articleWeightDTO.getArticleWeightId()).isEqualTo(articleWeightEntity.getArticleWeightId());
        assertThat(articleWeightDTO.getArticleId()).isEqualTo(articleWeightEntity.getArticleId());
        assertThat(articleWeightDTO.getWeight()).isEqualTo(articleWeightEntity.getWeight());

        System.out.println("Test testGetArticleWeightByArticleId passed!");

    }

    @Test
    public void testSaveNewArticleWeight() {

        when(articleWeightRepository.save(any(ArticleWeightEntity.class))).thenReturn(articleWeightEntity);

        ArticleWeightDTO articleWeightDTO = articleWeightService.saveNewArticleWeight(this.articleWeightDTO);

        assertThat(articleWeightDTO.getArticleWeightId()).isEqualTo(articleWeightDTO.getArticleWeightId());
        assertThat(articleWeightDTO.getArticleId()).isEqualTo(articleWeightDTO.getArticleId());
        assertThat(articleWeightDTO.getWeight()).isEqualTo(articleWeightDTO.getWeight());

        System.out.println("Test testSaveNewArticleWeight passed.");

    }

}
