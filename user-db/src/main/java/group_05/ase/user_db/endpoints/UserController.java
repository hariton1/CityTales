package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserDTO;
import group_05.ase.user_db.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            return new ArrayList<UserDTO>(); //"An internal server error occurred => " + e.getMessage();
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
    public void updateUserById(@PathVariable("userId") UUID userId, @RequestBody UserDTO updatedValues) {
        try {
            this.userService.updateUserById(userId, updatedValues);
        } catch (Exception e) {
            logger.error("Error updating user with ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error updating user", e);
        }
    }

}
