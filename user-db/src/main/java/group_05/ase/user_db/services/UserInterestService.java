package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserInterestEntity;
import group_05.ase.user_db.repositories.UserInterestRepository;
import group_05.ase.user_db.restData.UserInterestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserInterestService {

    @Autowired
    UserInterestRepository repository;

    public List<UserInterestDTO> getAllUserInterests() {

        ArrayList<UserInterestDTO> userInterests = new ArrayList<>();
        List<UserInterestEntity> tmp = this.repository.findAll();

        for(UserInterestEntity userInterest : tmp) {
            userInterests.add(new UserInterestDTO(userInterest.getUserId(), userInterest.getInterestId(), userInterest.getCreDat()));
        }

        return userInterests;

    }

}
