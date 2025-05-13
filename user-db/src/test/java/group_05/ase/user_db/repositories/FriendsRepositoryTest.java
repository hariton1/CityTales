package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.FriendsEntity;
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
public class FriendsRepositoryTest {

    @Autowired
    FriendsRepository friendsRepository;

    private final FriendsEntity friendsEntity = new FriendsEntity(
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            null
    );

    @Test
    public void testFindByFriendsId() {

        FriendsEntity tmp = friendsRepository.findByFriendsId(1);

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFriendsId()).isEqualTo(friendsEntity.getFriendsId());
        assertThat(tmp.getFriendOne()).isEqualTo(friendsEntity.getFriendOne());
        assertThat(tmp.getFriendTwo()).isEqualTo(friendsEntity.getFriendTwo());

        System.out.println("Test testFindByFriendsId passed!");

    }

    @Test
    public void testFindByFriendOne() {

        ArrayList<FriendsEntity> tmp = new ArrayList<>(friendsRepository.findByFriendOne(UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b")));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getFriendsId()).isEqualTo(friendsEntity.getFriendsId());
        assertThat(tmp.getFirst().getFriendOne()).isEqualTo(friendsEntity.getFriendOne());
        assertThat(tmp.getFirst().getFriendTwo()).isEqualTo(friendsEntity.getFriendTwo());

        System.out.println("Test testFindByFriendOne passed!");

    }

    @Test
    public void testFindByFriendTwo() {

        ArrayList<FriendsEntity> tmp = new ArrayList<>(friendsRepository.findByFriendOne(UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b")));

        assertThat(tmp).isNotNull();
        assertThat(tmp.getFirst().getFriendsId()).isEqualTo(friendsEntity.getFriendsId());
        assertThat(tmp.getFirst().getFriendOne()).isEqualTo(friendsEntity.getFriendOne());
        assertThat(tmp.getFirst().getFriendTwo()).isEqualTo(friendsEntity.getFriendTwo());

        System.out.println("Test testFindByFriendTwo passed!");

    }

}
