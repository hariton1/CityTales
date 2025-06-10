package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserDTO;
import group_05.ase.user_db.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getAllUsers() {
        try {
            return this.userService.getAllUsers();
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage());
            throw new RuntimeException("Error fetching users", e);
        }
    }

    @GetMapping("/id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserById(@PathVariable("userId") UUID userId) {
        try {
            return this.userService.getUserById(userId);
        } catch (Exception e) {
            logger.error("Error fetching user with ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching user", e);

        }
    }

    @GetMapping("/email/id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public String getUserEmailById(@PathVariable("userId") UUID userId) {
        try {
            UserDTO userDTO = this.userService.getUserById(userId);
            return userDTO.getEmail();
        } catch (Exception e) {
            logger.error("Error fetching username with ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching username", e);
        }
    }

    @DeleteMapping("/id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable("userId") UUID userId) {
        try {
            this.userService.deleteUserById(userId);
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @PatchMapping("/id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUserById(@PathVariable("userId") UUID userId, @RequestBody UserDTO updatedValues) {
        try {
            logger.info("Updating user with ID {}: {}", userId, updatedValues);
            return this.userService.updateUserById(userId, updatedValues);
        } catch (Exception e) {
            logger.error("Error updating user with ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error updating user", e);
        }
    }
}
