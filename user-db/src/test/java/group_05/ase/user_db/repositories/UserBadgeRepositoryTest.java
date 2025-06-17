package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserBadgeEntity;
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
public class UserBadgeRepositoryTest {

    @Autowired
    UserBadgeRepository userBadgeRepository;

    private final UserBadgeEntity userBadgeEntity = new UserBadgeEntity(
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            21547,
            null
    );

    @Test
    public void testFindAllByUserIdOrderByUserBadgeIdAsc() {

        /*ArrayList<UserBadgeEntity> tmp = new ArrayList<>(userBadgeRepository.findAllByUserIdOrderByUserBadgeIdAsc(userBadgeEntity.getUserId()));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getUserBadgeId()).isEqualTo(userBadgeEntity.getUserBadgeId());
        assertThat(tmp.getFirst().getUserId()).isEqualTo(userBadgeEntity.getUserId());
        assertThat(tmp.getFirst().getArticleId()).isEqualTo(userBadgeEntity.getArticleId());*/

    }

    @Test
    public void testFindByNonExistUserId() {

        /*ArrayList<UserBadgeEntity> tmp = new ArrayList<>(userBadgeRepository.findAllByUserIdOrderByUserBadgeIdAsc(UUID.fromString("00000000-0000-0000-0000-000000000000")));

        assertThat(tmp).isNotNull();
        assertThat(tmp).isEqualTo(new ArrayList<>());*/

    }

    @Test
    public void testFindAllByArticleIdOrderByUserBadgeIdAsc() {

        /*ArrayList<UserBadgeEntity> tmp = new ArrayList<>(userBadgeRepository.findAllByArticleIdOrderByUserBadgeIdAsc(userBadgeEntity.getArticleId()));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getUserBadgeId()).isEqualTo(userBadgeEntity.getUserBadgeId());
        assertThat(tmp.getFirst().getUserId()).isEqualTo(userBadgeEntity.getUserId());
        assertThat(tmp.getFirst().getArticleId()).isEqualTo(userBadgeEntity.getArticleId());*/

    }

}
