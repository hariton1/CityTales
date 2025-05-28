package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserBadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadgeEntity, Integer> {

    List<UserBadgeEntity> findAllByUserId(UUID userId);

    List<UserBadgeEntity> findAllByArticleId(int articleId);

}
