package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.InterestEntity;
import group_05.ase.user_db.repositories.InterestRepository;
import group_05.ase.user_db.restData.InterestDTO;
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

    public InterestDTO getInterestById(int interestId) {

        InterestEntity tmp = this.repository.findByInterestId(interestId);

        return new InterestDTO(tmp.getInterestId(), tmp.getInterestTypeId(),
                tmp.getInterestName(), tmp.getDescription());

    }

    public InterestDTO getInterestByInterestTypeId(int interestTypeId) {

        InterestEntity tmp = this.repository.findByInterestTypeId(interestTypeId);

        return new InterestDTO(tmp.getInterestId(), tmp.getInterestTypeId(),
                tmp.getInterestName(), tmp.getDescription());
    }

    public InterestDTO getInterestByName(String interestName) {

        InterestEntity tmp = this.repository.findByInterestName(interestName);

        return new InterestDTO(tmp.getInterestId(), tmp.getInterestTypeId(),
                               tmp.getInterestName(), tmp.getDescription());
    }

    public List<InterestDTO> getInterestsByDescriptionLike(String description) {

        ArrayList<InterestDTO> interests = new ArrayList<>();
        List<InterestEntity> tmp = this.repository.findByDescriptionContaining(description);

        for(InterestEntity interest : tmp) {
            interests.add(new InterestDTO(interest.getInterestId(), interest.getInterestTypeId(),
                    interest.getInterestName(), interest.getDescription()));
        }

        return interests;

    }

    public void saveNewInterest(InterestDTO interestDTO) {

        InterestEntity tmp = new InterestEntity();

        tmp.setInterestTypeId(interestDTO.getInterestTypeId());
        tmp.setInterestName(interestDTO.getInterestName());
        tmp.setDescription(interestDTO.getDescription());

        this.repository.save(tmp);

    }

}
