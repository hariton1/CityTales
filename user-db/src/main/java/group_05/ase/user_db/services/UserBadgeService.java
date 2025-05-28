package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserBadgeEntity;
import group_05.ase.user_db.repositories.UserBadgeRepository;
import group_05.ase.user_db.restData.UserBadgeDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserBadgeService {

    private final UserBadgeRepository repository;

    public UserBadgeService(UserBadgeRepository repository) {
        this.repository = repository;
    }

    public List<UserBadgeDTO> getAllUserBadges() {

        ArrayList<UserBadgeDTO> userBadges = new ArrayList<>();
        List<UserBadgeEntity> tmp = this.repository.findAll();

        for(UserBadgeEntity userBadge : tmp) {
            userBadges.add(new UserBadgeDTO(
                    userBadge.getUserBadgeId(),
                    userBadge.getUserId(),
                    userBadge.getArticleId(),
                    userBadge.getEarnedAt()
            ));
        }

        return userBadges;

    }

    public List<UserBadgeDTO> getUserBadgesByUserId(UUID userId) {

        ArrayList<UserBadgeDTO> userBadges = new ArrayList<>();
        List<UserBadgeEntity> tmp = this.repository.findAllByUserId(userId);

        for(UserBadgeEntity userBadge : tmp) {
            userBadges.add(new UserBadgeDTO(
                    userBadge.getUserBadgeId(),
                    userBadge.getUserId(),
                    userBadge.getArticleId(),
                    userBadge.getEarnedAt()
            ));
        }

        return userBadges;

    }

    public List<UserBadgeDTO> getUserBadgesBaArticleId(int articleId) {

        ArrayList<UserBadgeDTO> userBadges = new ArrayList<>();
        List<UserBadgeEntity> tmp = this.repository.findAllByArticleId(articleId);

        for(UserBadgeEntity userBadge : tmp) {
            userBadges.add(new UserBadgeDTO(
                    userBadge.getUserBadgeId(),
                    userBadge.getUserId(),
                    userBadge.getArticleId(),
                    userBadge.getEarnedAt()
            ));
        }

        return userBadges;

    }

    public UserBadgeDTO saveNewBadge(UserBadgeDTO userBadgeDTO) {

        UserBadgeEntity tmp = new UserBadgeEntity();

        tmp.setUserId(userBadgeDTO.getUserId());
        tmp.setArticleId(userBadgeDTO.getArticleId());
        tmp.setEarnedAt(userBadgeDTO.getEarnedAt());

        UserBadgeEntity insertedBadge = this.repository.save(tmp);

        return new UserBadgeDTO(
                insertedBadge.getUserBadgeId(),
                insertedBadge.getUserId(),
                insertedBadge.getArticleId(),
                insertedBadge.getEarnedAt()
        );

    }

}
