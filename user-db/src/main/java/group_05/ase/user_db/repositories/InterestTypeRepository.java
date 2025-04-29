package group_05.ase.user_db.repositories;

import group_05.ase.user_db.entities.InterestTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestTypeRepository extends JpaRepository<InterestTypeEntity, Integer> {

    List<InterestTypeEntity> findAllByTypeName(String typeName);

}
