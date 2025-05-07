package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserPointEntity;
import group_05.ase.user_db.repositories.UserPointRepository;
import group_05.ase.user_db.restData.UserPointDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserPointService {

    private final UserPointRepository repository;

    public UserPointService(UserPointRepository repository) {
        this.repository = repository;
    }

    public List<UserPointDTO> getAllUserPoints() {

        ArrayList<UserPointDTO> userPoints = new ArrayList<>();
        List<UserPointEntity> tmp = this.repository.findAll();

        for(UserPointEntity userPoint : tmp) {
            userPoints.add(new UserPointDTO(userPoint.getUserPointId(), userPoint.getUserId(),
                    userPoint.getPoints(), userPoint.getEarnedAt()));
        }

        return userPoints;

    }

    public UserPointDTO getUserPointById(int userPointId) {

        UserPointEntity tmp = this.repository.findByUserPointId(userPointId);

        return new UserPointDTO(tmp.getUserPointId(), tmp.getUserId(), tmp.getPoints(), tmp.getEarnedAt());

    }

    public List<UserPointDTO> getUserPointsByUserId(UUID userId) {

        ArrayList<UserPointDTO> userPoints = new ArrayList<>();
        List<UserPointEntity> tmp = this.repository.findByUserId(userId);

        for(UserPointEntity userPoint : tmp) {
            userPoints.add(new UserPointDTO(userPoint.getUserPointId(), userPoint.getUserId(),
                    userPoint.getPoints(), userPoint.getEarnedAt()));
        }

        return userPoints;

    }

    public void saveNewPoints(UserPointDTO userPointDTO) {

        UserPointEntity tmp = new UserPointEntity();

        tmp.setUserId(userPointDTO.getUserId());
        tmp.setPoints(userPointDTO.getPoints());
        tmp.setEarnedAt(userPointDTO.getEarnedAt());

        this.repository.save(tmp);

    }

}
