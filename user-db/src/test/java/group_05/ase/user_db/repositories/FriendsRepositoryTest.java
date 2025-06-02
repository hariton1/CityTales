package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.FriendsEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FriendsRepositoryTest {

    @Autowired
    private FriendsRepository friendsRepository;

    private UUID uuid1;
    private UUID uuid2;
    private FriendsEntity testEntity;

    @BeforeEach
    void setUp() {
        uuid1 = UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b");
        uuid2 = UUID.fromString("5be46711-4f9c-468b-a6dc-0dce03f3b318");

        testEntity = new FriendsEntity();
        testEntity.setFriendOne(uuid1);
        testEntity.setFriendTwo(uuid2);
        // creDat NICHT notwendig wegen @PrePersist
        friendsRepository.save(testEntity);
    }

    @Test
    void testFindByFriendsId() {
        FriendsEntity found = friendsRepository.findByFriendsId(testEntity.getFriendsId());
        assertThat(found).isNotNull();
        assertThat(found.getFriendOne()).isEqualTo(uuid1);
        assertThat(found.getFriendTwo()).isEqualTo(uuid2);
    }

    @Test
    void testFindByFriendOne() {
        List<FriendsEntity> found = friendsRepository.findByFriendOne(uuid1);
        assertThat(found).extracting(FriendsEntity::getFriendsId).contains(testEntity.getFriendsId());
    }

    @Test
    void testFindByFriendTwo() {
        List<FriendsEntity> found = friendsRepository.findByFriendTwo(uuid2);
        assertThat(found).extracting(FriendsEntity::getFriendsId).contains(testEntity.getFriendsId());
    }
}
