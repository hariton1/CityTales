package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.UserDTO;
import group_05.ase.user_db.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

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

}
