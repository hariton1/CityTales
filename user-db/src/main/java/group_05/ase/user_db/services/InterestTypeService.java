package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.InterestTypeEntity;
import group_05.ase.user_db.repositories.InterestTypeRepository;
import group_05.ase.user_db.restData.InterestTypeDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterestTypeService {

    private final InterestTypeRepository repository;

    public InterestTypeService(InterestTypeRepository repository) {
        this.repository = repository;
    }

    public List<InterestTypeDTO> getAllInterestTypes() {

        ArrayList<InterestTypeDTO> interestTypes = new ArrayList<>();
        List<InterestTypeEntity> tmp = this.repository.findAll();

        for(InterestTypeEntity interestType : tmp) {
            interestTypes.add(new InterestTypeDTO(interestType.getInterestTypeId(), interestType.getTypeName(),
                                                  interestType.getDescription(), interestType.getCreDat()));
        }

        return interestTypes;

    }

    public InterestTypeDTO getInterestById(int interestTypeId) {

        InterestTypeEntity tmp = this.repository.findByInterestTypeId(interestTypeId);

        return new InterestTypeDTO(tmp.getInterestTypeId(), tmp.getTypeName(),
                tmp.getDescription(), tmp.getCreDat());

    }

    public InterestTypeDTO getInterestByName(String name) {

        InterestTypeEntity tmp = this.repository.findByTypeName(name);

        return new InterestTypeDTO(tmp.getInterestTypeId(), tmp.getTypeName(),
                tmp.getDescription(), tmp.getCreDat());

    }

    public List<InterestTypeDTO> getInterestTypesByDescriptionLike(String description) {

        ArrayList<InterestTypeDTO> interestTypes = new ArrayList<>();
        List<InterestTypeEntity> tmp = this.repository.findByDescriptionContaining(description);

        for(InterestTypeEntity interestType : tmp) {
            interestTypes.add(new InterestTypeDTO(interestType.getInterestTypeId(), interestType.getTypeName(),
                    interestType.getDescription(), interestType.getCreDat()));
        }

        return interestTypes;

    }

    public void saveNewInterestType(InterestTypeDTO interestTypeDTO) {

        InterestTypeEntity tmp = new InterestTypeEntity();

        tmp.setTypeName(interestTypeDTO.getTypeName());
        tmp.setDescription(interestTypeDTO.getDescription());
        tmp.setCreDat(interestTypeDTO.getCreDat());

        this.repository.save(tmp);

    }

}
