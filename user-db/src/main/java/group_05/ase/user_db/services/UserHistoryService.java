package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserHistoryEntity;
import group_05.ase.user_db.repositories.UserHistoryRepository;
import group_05.ase.user_db.restData.UserHistoryDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserHistoryService {

    @Autowired
    UserHistoryRepository repository;

    public List<UserHistoryDTO> getAllUserHistories() {

        ArrayList<UserHistoryDTO> userHistories = new ArrayList<>();
        List<UserHistoryEntity> tmp = this.repository.findAll();

        for(UserHistoryEntity userHistory : tmp) {
            userHistories.add(new UserHistoryDTO(userHistory.getUserHistoryId(), userHistory.getUserId(), userHistory.getArticleId(),
                                                 userHistory.getOpenDt(), userHistory.getCloseDt()));
        }

        return userHistories;

    }

}
