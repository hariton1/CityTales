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

    private final InterestEntity friendsEntity = new InterestEntity(
            4,
            1,
            "building",
            "the person is interested in buildings serving as a medical centers"
    );

    @Test
    public void testFindByInterestId() {

        InterestEntity tmp = interestRepository.findByInterestId(4);

        assertThat(tmp).isNotNull();
        assertThat(tmp.getInterestId()).isEqualTo(friendsEntity.getInterestId());
        assertThat(tmp.getInterestTypeId()).isEqualTo(friendsEntity.getInterestTypeId());
        assertThat(tmp.getInterestName()).isEqualTo(friendsEntity.getInterestName());
        assertThat(tmp.getDescription()).isEqualTo(friendsEntity.getDescription());

        System.out.println("Test testFindByInterestId passed!");

    }

    @Test
    public void testFindByInterestTypeId() {

        InterestEntity tmp = interestRepository.findByInterestTypeId(1);

        assertThat(tmp).isNotNull();
        assertThat(tmp.getInterestId()).isEqualTo(friendsEntity.getInterestId());
        assertThat(tmp.getInterestTypeId()).isEqualTo(friendsEntity.getInterestTypeId());
        assertThat(tmp.getInterestName()).isEqualTo(friendsEntity.getInterestName());
        assertThat(tmp.getDescription()).isEqualTo(friendsEntity.getDescription());

        System.out.println("Test testFindByInterestTypeId passed!");

    }

    @Test
    public void testFindByInterestName() {

        InterestEntity tmp = interestRepository.findByInterestName("building");

        assertThat(tmp).isNotNull();
        assertThat(tmp.getInterestId()).isEqualTo(friendsEntity.getInterestId());
        assertThat(tmp.getInterestTypeId()).isEqualTo(friendsEntity.getInterestTypeId());
        assertThat(tmp.getInterestName()).isEqualTo(friendsEntity.getInterestName());
        assertThat(tmp.getDescription()).isEqualTo(friendsEntity.getDescription());

        System.out.println("Test testFindByInterestName passed!");

    }

    @Test
    public void testFindByDescriptionContaining() {

        ArrayList<InterestEntity> tmp = new ArrayList<>(interestRepository.findByDescriptionContaining("medical"));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getInterestId()).isEqualTo(friendsEntity.getInterestId());
        assertThat(tmp.getFirst().getInterestTypeId()).isEqualTo(friendsEntity.getInterestTypeId());
        assertThat(tmp.getFirst().getInterestName()).isEqualTo(friendsEntity.getInterestName());
        assertThat(tmp.getFirst().getDescription()).isEqualTo(friendsEntity.getDescription());

        System.out.println("Test testFindByDescriptionContaining passed!");

    }

}
