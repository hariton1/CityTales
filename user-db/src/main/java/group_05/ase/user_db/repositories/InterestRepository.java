package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.InterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<InterestEntity, Integer> {

    InterestEntity findByInterestId(int interestId);

    InterestEntity findByInterestNameEn(String interestNameEn);

    InterestEntity findByInterestNameDe(String interestNameDe);

    List<InterestEntity> findByDescriptionContaining(String description);

}
