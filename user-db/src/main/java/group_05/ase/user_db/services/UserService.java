package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.AuthUserEntity;
import group_05.ase.user_db.entities.UserDataEntity;
import group_05.ase.user_db.repositories.UserDataRepository;
import group_05.ase.user_db.repositories.UserRepository;
import group_05.ase.user_db.restData.UserDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final UserDataRepository userDataRepository;

    public UserService(UserRepository repository, UserDataRepository userDataRepository) {
        this.repository = repository;
        this.userDataRepository = userDataRepository;
    }

    public List<UserDTO> getAllUsers() {
        ArrayList<UserDTO> users = new ArrayList<>();
        List<AuthUserEntity> tmp = this.repository.findAll();

        for(AuthUserEntity user : tmp) {
            UserDataEntity userData = this.userDataRepository.findByUserId(user.getId());
            users.add(new UserDTO(user.getId(),user.getEmail(),user.getCreatedAt(), userData.getRoleName(), userData.getStatus()));
        }

        return users;
    }

    public UserDTO getUserById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        AuthUserEntity user = this.repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        UserDataEntity userData = this.userDataRepository.findByUserId(user.getId());

        return new UserDTO(user.getId(),user.getEmail(),user.getCreatedAt(), userData.getRoleName(), userData.getStatus());
    }

    public void deleteUserById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        //this.repository.deleteById(userId);
        UserDataEntity tmp = this.userDataRepository.findByUserId(userId);
        tmp.setStatus("Deleted");
        this.userDataRepository.save(tmp);
    }

    public UserDTO updateUserById(UUID userId, UserDTO updatedValues) {
        AuthUserEntity existingUser = this.repository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));


        if (updatedValues.getEmail() != null) {
            existingUser.setEmail(updatedValues.getEmail());
        }

        AuthUserEntity updatedUser = this.repository.save(existingUser);

        UserDataEntity tmp = this.userDataRepository.findByUserId(updatedUser.getId());
        tmp.setRoleName(updatedValues.getRole());
        tmp.setStatus(updatedValues.getStatus());
        this.userDataRepository.save(tmp);

        return new UserDTO(updatedUser.getId(),updatedUser.getEmail(),updatedUser.getCreatedAt(), tmp.getRoleName(), tmp.getStatus());
    }
}
