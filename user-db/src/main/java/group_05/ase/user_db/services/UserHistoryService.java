package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserHistoryEntity;
import group_05.ase.user_db.repositories.UserHistoryRepository;
import group_05.ase.user_db.restData.UserHistoryDTO;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserHistoryService {

    private final UserHistoryRepository repository;

    public UserHistoryService(UserHistoryRepository repository) {
        this.repository = repository;
    }

    public List<UserHistoryDTO> getAllUserHistories() {

        ArrayList<UserHistoryDTO> userHistories = new ArrayList<>();
        List<UserHistoryEntity> tmp = this.repository.findAll();

        for(UserHistoryEntity userHistory : tmp) {
            userHistories.add(new UserHistoryDTO(userHistory.getUserHistoryId(), userHistory.getUserId(), userHistory.getArticleId(),
                    userHistory.getOpenDt(), userHistory.getCloseDt()));
        }

        return userHistories;

    }

    public UserHistoryDTO getUserHistoriesById(int userHistoryId) {

        UserHistoryEntity tmp = this.repository.findByUserHistoryId(userHistoryId);

        return new UserHistoryDTO(tmp.getUserHistoryId(), tmp.getUserId(), tmp.getArticleId(),
                tmp.getOpenDt(), tmp.getCloseDt());

    }

    public List<UserHistoryDTO> getUserHistoriesByUserId(UUID userId) {

        ArrayList<UserHistoryDTO> userHistories = new ArrayList<>();
        List<UserHistoryEntity> tmp = this.repository.findAllByUserId(userId);

        for(UserHistoryEntity userHistory : tmp) {
            userHistories.add(new UserHistoryDTO(userHistory.getUserHistoryId(), userHistory.getUserId(), userHistory.getArticleId(),
                    userHistory.getOpenDt(), userHistory.getCloseDt()));
        }

        return userHistories;

    }

    public List<UserHistoryDTO> getUserHistoriesByArticleId(int articleId) {

        ArrayList<UserHistoryDTO> userHistories = new ArrayList<>();
        List<UserHistoryEntity> tmp = this.repository.findAllByArticleId(articleId);

        for(UserHistoryEntity userHistory : tmp) {
            userHistories.add(new UserHistoryDTO(userHistory.getUserHistoryId(), userHistory.getUserId(), userHistory.getArticleId(),
                    userHistory.getOpenDt(), userHistory.getCloseDt()));
        }

        return userHistories;

    }

    public void saveNewUserHistory(UserHistoryDTO userHistoryDTO) {

        UserHistoryEntity tmp = new UserHistoryEntity();

        tmp.setUserId(userHistoryDTO.getUserId());
        tmp.setArticleId(userHistoryDTO.getArticleId());
        tmp.setOpenDt(userHistoryDTO.getOpenDt());

        this.repository.save(tmp);

    }

    public void saveChangedUserHistory(UserHistoryDTO userHistoryDTO) {

        UserHistoryEntity tmp = new UserHistoryEntity();

        tmp.setUserHistoryId(userHistoryDTO.getUserHistoryId());
        tmp.setUserId(userHistoryDTO.getUserId());
        tmp.setArticleId(userHistoryDTO.getArticleId());
        tmp.setOpenDt(userHistoryDTO.getOpenDt());
        tmp.setCloseDt(userHistoryDTO.getCloseDt());

        this.repository.save(tmp);

    }

}
