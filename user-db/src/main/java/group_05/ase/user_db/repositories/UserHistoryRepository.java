package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistoryEntity, Integer> {

    UserHistoryEntity findByUserHistoryId(int userHistoryId);

    List<UserHistoryEntity> findAllByUserId(UUID userId);

    List<UserHistoryEntity> findAllByArticleId(int articleId);

}
