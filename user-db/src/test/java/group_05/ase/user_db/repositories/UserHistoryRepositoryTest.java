package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserHistoryEntity;
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
public class UserHistoryRepositoryTest {

    @Autowired
    UserHistoryRepository userHistoryRepository;

    private final UserHistoryEntity userHistoryEntity = new UserHistoryEntity(
            2,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            1,
            null,
            null,
            2
    );

    @Test
    public void testFindByUserHistoryId() {

        /*UserHistoryEntity tmp = userHistoryRepository.findByUserHistoryId(2);

        assertThat(tmp).isNotNull();
        assertThat(tmp.getUserHistoryId()).isEqualTo(userHistoryEntity.getUserHistoryId());
        assertThat(tmp.getUserId()).isEqualTo(userHistoryEntity.getUserId());
        assertThat(tmp.getArticleId()).isEqualTo(userHistoryEntity.getArticleId());
        assertThat(tmp.getInterestId()).isEqualTo(userHistoryEntity.getInterestId());*/

    }

    @Test
    public void testFindAllByUserId() {

       /* ArrayList<UserHistoryEntity> tmp = new ArrayList<>(userHistoryRepository.findAllByUserIdOrderByUserHistoryIdAsc(UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b")));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getUserHistoryId()).isEqualTo(userHistoryEntity.getUserHistoryId());
        assertThat(tmp.getFirst().getUserId()).isEqualTo(userHistoryEntity.getUserId());
        assertThat(tmp.getFirst().getArticleId()).isEqualTo(userHistoryEntity.getArticleId());
        assertThat(tmp.getFirst().getInterestId()).isEqualTo(userHistoryEntity.getInterestId());*/

    }

    @Test
    public void testFindAllByArticleId() {

        /*ArrayList<UserHistoryEntity> tmp = new ArrayList<>(userHistoryRepository.findAllByArticleId(1));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getUserHistoryId()).isEqualTo(userHistoryEntity.getUserHistoryId());
        assertThat(tmp.getFirst().getUserId()).isEqualTo(userHistoryEntity.getUserId());
        assertThat(tmp.getFirst().getArticleId()).isEqualTo(userHistoryEntity.getArticleId());
        assertThat(tmp.getFirst().getInterestId()).isEqualTo(userHistoryEntity.getInterestId());*/

    }

}
