package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.InterestEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest()
public class InterestRepositoryTest {

    @Autowired
    InterestRepository interestRepository;

    private final InterestEntity interestEntity = new InterestEntity(
            4,
            "buildings",
            "the person is interested in buildings",
            "Gebäude"
    );

    @Test
    public void testFindByInterestId() {

        InterestEntity tmp = interestRepository.findByInterestId(4);

        assertThat(tmp).isNotNull();
        assertThat(tmp.getInterestId()).isEqualTo(interestEntity.getInterestId());
        assertThat(tmp.getInterestNameEn()).isEqualTo(interestEntity.getInterestNameEn());
        assertThat(tmp.getInterestNameDe()).isEqualTo(interestEntity.getInterestNameDe());
        assertThat(tmp.getDescription()).isEqualTo(interestEntity.getDescription());

        System.out.println("Test testFindByInterestId passed!");

    }

    @Test
    public void testFindByInterestNameEn() {

        InterestEntity tmp = interestRepository.findByInterestNameEn("buildings");

        assertThat(tmp).isNotNull();
        assertThat(tmp.getInterestId()).isEqualTo(interestEntity.getInterestId());
        assertThat(tmp.getInterestNameEn()).isEqualTo(interestEntity.getInterestNameEn());
        assertThat(tmp.getInterestNameDe()).isEqualTo(interestEntity.getInterestNameDe());
        assertThat(tmp.getDescription()).isEqualTo(interestEntity.getDescription());

        System.out.println("Test testFindByInterestNameEn passed!");

    }

    @Test
    public void testFindByInterestNameDe() {

        InterestEntity tmp = interestRepository.findByInterestNameDe("Gebäude");

        assertThat(tmp).isNotNull();
        assertThat(tmp.getInterestId()).isEqualTo(interestEntity.getInterestId());
        assertThat(tmp.getInterestNameEn()).isEqualTo(interestEntity.getInterestNameEn());
        assertThat(tmp.getInterestNameDe()).isEqualTo(interestEntity.getInterestNameDe());
        assertThat(tmp.getDescription()).isEqualTo(interestEntity.getDescription());

        System.out.println("Test testFindByInterestNameDe passed!");

    }

    @Test
    public void testFindByDescriptionContaining() {

        ArrayList<InterestEntity> tmp = new ArrayList<>(interestRepository.findByDescriptionContaining("build"));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getInterestId()).isEqualTo(interestEntity.getInterestId());
        assertThat(tmp.getFirst().getInterestNameEn()).isEqualTo(interestEntity.getInterestNameEn());
        assertThat(tmp.getFirst().getInterestNameDe()).isEqualTo(interestEntity.getInterestNameDe());
        assertThat(tmp.getFirst().getDescription()).isEqualTo(interestEntity.getDescription());

        System.out.println("Test testFindByDescriptionContaining passed!");

    }

}
