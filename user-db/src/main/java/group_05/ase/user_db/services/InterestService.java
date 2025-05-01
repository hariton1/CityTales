package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.InterestEntity;
import group_05.ase.user_db.repositories.InterestRepository;
import group_05.ase.user_db.restData.InterestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterestService {

    private final InterestRepository repository;

    public InterestService(InterestRepository repository) {
        this.repository = repository;
    }

    public List<InterestDTO> getAllInterests() {

        ArrayList<InterestDTO> interests = new ArrayList<>();
        List<InterestEntity> tmp = this.repository.findAll();

        for(InterestEntity interest : tmp) {
            interests.add(new InterestDTO(interest.getInterestId(), interest.getInterestTypeId(),
                                          interest.getInterestName(), interest.getDescription()));
        }

        return interests;
    }

}
