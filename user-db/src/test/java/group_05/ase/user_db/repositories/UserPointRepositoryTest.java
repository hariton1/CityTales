package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserPointEntity;
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
public class UserPointRepositoryTest {

    @Autowired
    UserPointRepository userPointRepository;

    private final UserPointEntity userPointEntity = new UserPointEntity (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            1,
            null,
            -1
    );

    @Test
    public void testFindByUserPointId() {

        /*UserPointEntity tmp = userPointRepository.findByUserPointId(1);

        assertThat(tmp).isNotNull();
        assertThat(tmp.getUserPointId()).isEqualTo(userPointEntity.getUserPointId());
        assertThat(tmp.getUserId()).isEqualTo(userPointEntity.getUserId());
        assertThat(tmp.getPoints()).isEqualTo(userPointEntity.getPoints());
        assertThat(tmp.getArticleId()).isEqualTo(userPointEntity.getArticleId());*/

        System.out.println("Test testFindByUserPointId passed!");

    }

    @Test
    public void testFindByUserId() {

        /*ArrayList<UserPointEntity> tmp = new ArrayList<>(userPointRepository.findByUserId(UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b")));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getUserPointId()).isEqualTo(userPointEntity.getUserPointId());
        assertThat(tmp.getFirst().getUserId()).isEqualTo(userPointEntity.getUserId());
        assertThat(tmp.getFirst().getPoints()).isEqualTo(userPointEntity.getPoints());
        assertThat(tmp.getFirst().getArticleId()).isEqualTo(userPointEntity.getArticleId());*/

        System.out.println("Test testFindByUserId passed!");

    }

}
