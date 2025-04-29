package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.InterestTypeEntity;
import group_05.ase.user_db.repositories.InterestTypeRepository;
import group_05.ase.user_db.restData.InterestTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterestTypeService {

    @Autowired
    InterestTypeRepository repository;

    public List<InterestTypeDTO> getAllInterestTypes() {

        ArrayList<InterestTypeDTO> interestTypes = new ArrayList<>();
        List<InterestTypeEntity> tmp = this.repository.findAll();

        for(InterestTypeEntity interestType : tmp) {
            interestTypes.add(new InterestTypeDTO(interestType.getInterestTypeId(), interestType.getTypeName(),
                                                  interestType.getDescription(), interestType.getCreDat()));
        }

        return interestTypes;

    }

}
