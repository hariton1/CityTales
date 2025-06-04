package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.ArticleWeightEntity;
import group_05.ase.user_db.entities.UserHistoryEntity;
import group_05.ase.user_db.repositories.ArticleWeightRepository;
import group_05.ase.user_db.repositories.UserHistoryRepository;
import group_05.ase.user_db.restData.ArticleWeightDTO;
import group_05.ase.user_db.restData.UserHistoryDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserHistoryService {

    private final UserHistoryRepository repository;
    private final ArticleWeightRepository articleWeightRepository;
    private final ArticleWeightService articleWeightService;
    private final float weightIncrease = 0.01F;

    public UserHistoryService(UserHistoryRepository repository, ArticleWeightRepository articleWeightRepository,
                              ArticleWeightService articleWeightService) {
        this.repository = repository;
        this.articleWeightRepository = articleWeightRepository;
        this.articleWeightService = articleWeightService;
    }

    public List<UserHistoryDTO> getAllUserHistories() {

        ArrayList<UserHistoryDTO> userHistories = new ArrayList<>();
        List<UserHistoryEntity> tmp = this.repository.findAll();

        for(UserHistoryEntity userHistory : tmp) {
            userHistories.add(new UserHistoryDTO(userHistory.getUserHistoryId(), userHistory.getUserId(), userHistory.getArticleId(),
                    userHistory.getOpenDt(), userHistory.getCloseDt(), userHistory.getInterestId()));
        }

        return userHistories;

    }

    public UserHistoryDTO getUserHistoriesById(int userHistoryId) {

        UserHistoryEntity tmp = this.repository.findByUserHistoryId(userHistoryId);

        return new UserHistoryDTO(tmp.getUserHistoryId(), tmp.getUserId(), tmp.getArticleId(),
                tmp.getOpenDt(), tmp.getCloseDt(), tmp.getInterestId());

    }

    public List<UserHistoryDTO> getUserHistoriesByUserId(UUID userId) {

        ArrayList<UserHistoryDTO> userHistories = new ArrayList<>();
        List<UserHistoryEntity> tmp = this.repository.findAllByUserIdOrderByUserHistoryIdAsc(userId);

        for(UserHistoryEntity userHistory : tmp) {
            userHistories.add(new UserHistoryDTO(userHistory.getUserHistoryId(), userHistory.getUserId(), userHistory.getArticleId(),
                    userHistory.getOpenDt(), userHistory.getCloseDt(), userHistory.getInterestId()));
        }

        return userHistories;

    }

    public List<UserHistoryDTO> getUserHistoriesByArticleId(int articleId) {

        ArrayList<UserHistoryDTO> userHistories = new ArrayList<>();
        List<UserHistoryEntity> tmp = this.repository.findAllByArticleId(articleId);

        for(UserHistoryEntity userHistory : tmp) {
            userHistories.add(new UserHistoryDTO(userHistory.getUserHistoryId(), userHistory.getUserId(), userHistory.getArticleId(),
                    userHistory.getOpenDt(), userHistory.getCloseDt(), userHistory.getInterestId()));
        }

        return userHistories;

    }

    public UserHistoryDTO saveNewUserHistory(UserHistoryDTO userHistoryDTO) {

        UserHistoryEntity tmp = new UserHistoryEntity();

        tmp.setUserId(userHistoryDTO.getUserId());
        tmp.setArticleId(userHistoryDTO.getArticleId());
        tmp.setOpenDt(userHistoryDTO.getOpenDt());
        tmp.setInterestId(userHistoryDTO.getInterestId());

        UserHistoryEntity insertedUserHistory = this.repository.save(tmp);

        /* *** update article weight *** */
        ArticleWeightEntity articleWeightEntity = this.articleWeightRepository.findByArticleId(userHistoryDTO.getArticleId());

        if(articleWeightEntity != null) {
            articleWeightEntity.setWeight(articleWeightEntity.getWeight() + weightIncrease);
            this.articleWeightRepository.save(articleWeightEntity);
        } else {
            this.articleWeightService.saveNewArticleWeight(new ArticleWeightDTO(-1, userHistoryDTO.getArticleId(), weightIncrease));
        }
        /* ***************************** */

        return new UserHistoryDTO(insertedUserHistory.getUserHistoryId(), insertedUserHistory.getUserId(), insertedUserHistory.getArticleId(),
                insertedUserHistory.getOpenDt(), insertedUserHistory.getCloseDt(), insertedUserHistory.getInterestId());

    }

    public void saveChangedUserHistory(UserHistoryDTO userHistoryDTO) {

        UserHistoryEntity tmp = new UserHistoryEntity();

        tmp.setUserHistoryId(userHistoryDTO.getUserHistoryId());
        tmp.setUserId(userHistoryDTO.getUserId());
        tmp.setArticleId(userHistoryDTO.getArticleId());
        tmp.setOpenDt(userHistoryDTO.getOpenDt());
        tmp.setCloseDt(userHistoryDTO.getCloseDt());
        tmp.setInterestId(userHistoryDTO.getInterestId());

        this.repository.save(tmp);

    }

}
