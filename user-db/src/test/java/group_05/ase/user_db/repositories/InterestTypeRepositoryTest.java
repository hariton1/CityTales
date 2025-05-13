package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.InterestTypeEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest()
public class InterestTypeRepositoryTest {

    @Autowired
    InterestTypeRepository interestTypeRepository;

    private final InterestTypeEntity interestTypeEntity = new InterestTypeEntity(
            1,
            "medic",
            "the person is interested in medical fun-facts",
            null
    );

    @Test
    public void testFindByInterestTypeId() {

        InterestTypeEntity tmp = interestTypeRepository.findByInterestTypeId(1);

        assertThat(tmp).isNotNull();
        assertThat(tmp.getInterestTypeId()).isEqualTo(interestTypeEntity.getInterestTypeId());
        assertThat(tmp.getTypeName()).isEqualTo(interestTypeEntity.getTypeName());
        assertThat(tmp.getDescription()).isEqualTo(interestTypeEntity.getDescription());

        System.out.println("Test testFindByInterestTypeId passed!");

    }

    @Test
    public void testFindByTypeName() {

        InterestTypeEntity tmp = interestTypeRepository.findByTypeName("medic");

        assertThat(tmp).isNotNull();
        assertThat(tmp.getInterestTypeId()).isEqualTo(interestTypeEntity.getInterestTypeId());
        assertThat(tmp.getTypeName()).isEqualTo(interestTypeEntity.getTypeName());
        assertThat(tmp.getDescription()).isEqualTo(interestTypeEntity.getDescription());

        System.out.println("Test testFindByTypeName passed!");

    }

    @Test
    public void testFindByDescriptionContaining() {

        ArrayList<InterestTypeEntity> tmp = new ArrayList<>(interestTypeRepository.findByDescriptionContaining("medical"));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getInterestTypeId()).isEqualTo(interestTypeEntity.getInterestTypeId());
        assertThat(tmp.getFirst().getTypeName()).isEqualTo(interestTypeEntity.getTypeName());
        assertThat(tmp.getFirst().getDescription()).isEqualTo(interestTypeEntity.getDescription());

        System.out.println("Test testFindByDescriptionContaining passed!");

    }

}
