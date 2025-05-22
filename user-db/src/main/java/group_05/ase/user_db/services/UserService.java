package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserEntity;
import group_05.ase.user_db.repositories.UserRepository;
import group_05.ase.user_db.restData.UserDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<UserDTO> getAllUsers() {
        ArrayList<UserDTO> users = new ArrayList<>();
        List<UserEntity> tmp = this.repository.findAll();

        for(UserEntity user : tmp) {
            users.add(new UserDTO(user.getId(), user.getSupabaseId(), user.getEmail(),
                    user.getCreatedAt(), user.getDisplayName(), user.isActive()));
        }

        return users;
    }

}
