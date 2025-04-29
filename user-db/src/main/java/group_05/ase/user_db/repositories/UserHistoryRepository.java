package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.UserHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistoryEntity, Integer> {

    List<UserHistoryEntity> findAllByArticleId(int articleId);

    List<UserHistoryEntity> findAllByUserId(String userId);

}
