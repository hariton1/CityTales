package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserPointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserPointRepository extends JpaRepository<UserPointEntity, Integer> {

    UserPointEntity findByUserPointId(Integer userPointId);

    List<UserPointEntity> findByUserId(UUID userId);

    @Query("SELECT up FROM UserPointEntity up WHERE up.userId = ?1 AND up.articleId = ?2 AND function('DATE', up.earnedAt) = function('DATE', ?3) AND up.points = ?4")
    UserPointEntity findByUserIdAndArticleIdAndDay(UUID userId, int articleId, LocalDateTime earnedAt, int points);

}
