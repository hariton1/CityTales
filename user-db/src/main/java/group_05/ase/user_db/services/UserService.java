package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserEntity;
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

    public UserDTO getUserById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        UserEntity user = this.repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return new UserDTO(user.getId(), user.getSupabaseId(), user.getEmail(), user.getCreatedAt(), user.getDisplayName(), user.isActive());
    }

    public void deleteUserById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.repository.deleteById(userId);
    }

    public UserDTO updateUserById(UUID userId, UserDTO updatedValues) {
        UserEntity existingUser = this.repository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (updatedValues.getDisplayName() != null) {
            existingUser.setDisplayName(updatedValues.getDisplayName());
        }

        if (updatedValues.getEmail() != null) {
            existingUser.setEmail(updatedValues.getEmail());
        }

        if (updatedValues.getIsActive() != null) {
            existingUser.setActive(updatedValues.getIsActive());
        }

        UserEntity updatedUser = this.repository.save(existingUser);

        return new UserDTO(updatedUser.getId(), updatedUser.getSupabaseId(), updatedUser.getEmail(), updatedUser.getCreatedAt(), updatedUser.getDisplayName(), updatedUser.isActive());
    }

}
