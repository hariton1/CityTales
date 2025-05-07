package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.FriendsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FriendsRepository extends JpaRepository<FriendsEntity, Integer> {

    FriendsEntity findByFriendsId(Integer friendsId);

    List<FriendsEntity> findByFriendOne(UUID friendOne);

    List<FriendsEntity> findByFriendTwo(UUID friendTwo);

}
