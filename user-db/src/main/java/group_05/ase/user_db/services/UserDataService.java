package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserDataEntity;
import group_05.ase.user_db.repositories.UserDataRepository;
import group_05.ase.user_db.restData.UserDataDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserDataService {

    private final UserDataRepository repository;

    public UserDataService(UserDataRepository repository) {
        this.repository = repository;
    }

    public List<UserDataDTO> getAllUserData() {

        ArrayList<UserDataDTO> userData = new ArrayList<>();
        List<UserDataEntity> tmp = this.repository.findAll();

        for(UserDataEntity data : tmp) {
            userData.add(new UserDataDTO(data.getUserDataId(), data.getUserId(), data.getRoleName(),
                    data.getStatus(), data.getCreDat()));
        }

        return userData;

    }

    public UserDataDTO getUserDataById(int userDataId) {

        UserDataEntity tmp = this.repository.findByUserDataId(userDataId);

        return new UserDataDTO(tmp.getUserDataId(), tmp.getUserId(), tmp.getRoleName(), tmp.getStatus(), tmp.getCreDat());

    }

    public UserDataDTO getUserDataByUserId(UUID userId) {

        UserDataEntity tmp = this.repository.findByUserId(userId);

        return new UserDataDTO(tmp.getUserDataId(), tmp.getUserId(), tmp.getRoleName(), tmp.getStatus(), tmp.getCreDat());

    }

    public UserDataDTO saveUserData(UserDataDTO userDataDTO) {

        UserDataEntity tmp = this.repository.findByUserId(userDataDTO.getUserId());

        if(tmp == null) {
            tmp = new UserDataEntity();

            tmp.setUserId(userDataDTO.getUserId());
            tmp.setRoleName(userDataDTO.getRoleName());
            tmp.setStatus(userDataDTO.getStatus());
            tmp.setCreDat(userDataDTO.getCreDat());

            tmp = this.repository.save(tmp);
        } else {
            tmp.setRoleName(userDataDTO.getRoleName());
            tmp.setStatus(userDataDTO.getStatus());

            tmp = this.repository.save(tmp);
        }

        return new UserDataDTO(tmp.getUserDataId(), tmp.getUserId(), tmp.getRoleName(), tmp.getStatus(), tmp.getCreDat());

    }

}
