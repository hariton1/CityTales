package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserInterestEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest()
public class UserInterestRepositoryTest {

    @Autowired
    UserInterestRepository userInterestRepository;

    private final UserInterestEntity userInterestEntity = new UserInterestEntity (
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            5,
            null,
            1
    );

    @Test
    public void testFindByUserId() {

        ArrayList<UserInterestEntity> tmp = new ArrayList<>(userInterestRepository.findByUserIdOrderByInterestIdAsc(this.userInterestEntity.getUserId()));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getUserId()).isEqualTo(userInterestEntity.getUserId());
        assertThat(tmp.getFirst().getInterestId()).isEqualTo(userInterestEntity.getInterestId());
        assertThat(tmp.getFirst().getInterestWeight()).isEqualTo(userInterestEntity.getInterestWeight());

        System.out.println("Test testFindByUserId passed!");

    }

    @Test
    public void testFindByInterestId() {

        ArrayList<UserInterestEntity> tmp = new ArrayList<>(userInterestRepository.findByInterestIdOrderByInterestIdAsc(this.userInterestEntity.getInterestId()));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getUserId()).isEqualTo(userInterestEntity.getUserId());
        assertThat(tmp.getFirst().getInterestId()).isEqualTo(userInterestEntity.getInterestId());
        assertThat(tmp.getFirst().getInterestWeight()).isEqualTo(userInterestEntity.getInterestWeight());

        System.out.println("Test testFindByInterestId passed!");

    }

    @Test
    public void testFindByUserIdAndInterestId() {

        UserInterestEntity tmp = userInterestRepository.findByUserIdAndInterestId(this.userInterestEntity.getUserId(), this.userInterestEntity.getInterestId());

        assertThat(tmp).isNotNull();
        assertThat(tmp.getUserId()).isEqualTo(userInterestEntity.getUserId());
        assertThat(tmp.getInterestId()).isEqualTo(userInterestEntity.getInterestId());
        assertThat(tmp.getInterestWeight()).isEqualTo(userInterestEntity.getInterestWeight());

        System.out.println("Test testFindByUserIdAndInterestId passed!");

    }

}
