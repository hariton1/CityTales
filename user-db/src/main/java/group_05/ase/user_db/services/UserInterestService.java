package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserInterestEntity;
import group_05.ase.user_db.repositories.UserInterestRepository;
import group_05.ase.user_db.restData.UserInterestDTO;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserInterestService {

    private final UserInterestRepository repository;

    public UserInterestService(UserInterestRepository repository) {
        this.repository = repository;
    }

    public List<UserInterestDTO> getAllUserInterests() {

        ArrayList<UserInterestDTO> userInterests = new ArrayList<>();
        List<UserInterestEntity> tmp = this.repository.findAll();

        for(UserInterestEntity userInterest : tmp) {
            userInterests.add(new UserInterestDTO(userInterest.getUserId(), userInterest.getInterestId(), userInterest.getCreDat(), userInterest.getInterestWeight()));
        }

        return userInterests;

    }

    public List<UserInterestDTO> getUserInterestsByUserId(UUID userId) {

        ArrayList<UserInterestDTO> userInterests = new ArrayList<>();
        List<UserInterestEntity> tmp = this.repository.findByUserId(userId);

        for(UserInterestEntity userInterest : tmp) {
            userInterests.add(new UserInterestDTO(userInterest.getUserId(), userInterest.getInterestId(), userInterest.getCreDat(), userInterest.getInterestWeight()));
        }

        return userInterests;

    }

    public List<UserInterestDTO> getUserInterestsByInterestId(int interestId) {

        ArrayList<UserInterestDTO> userInterests = new ArrayList<>();
        List<UserInterestEntity> tmp = this.repository.findByInterestId(interestId);

        for(UserInterestEntity userInterest : tmp) {
            userInterests.add(new UserInterestDTO(userInterest.getUserId(), userInterest.getInterestId(), userInterest.getCreDat(), userInterest.getInterestWeight()));
        }

        return userInterests;

    }

    public void saveNewUserInterest(UserInterestDTO userInterestDTO) {

        UserInterestEntity tmp = new UserInterestEntity();

        tmp.setUserId(userInterestDTO.getUserId());
        tmp.setInterestId(userInterestDTO.getInterestId());
        tmp.setCreDat(userInterestDTO.getCreDat());
        tmp.setInterestWeight(userInterestDTO.getInterestWeight());

        this.repository.save(tmp);

    }

    public void deleteUserInterest(UserInterestDTO userInterestDTO) {

        UserInterestEntity tmp = new UserInterestEntity();

        tmp.setUserId(userInterestDTO.getUserId());
        tmp.setInterestId(userInterestDTO.getInterestId());

        this.repository.delete(tmp);

    }

}
