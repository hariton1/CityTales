package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.FriendsDTO;
import group_05.ase.user_db.services.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/friends")
public class FriendsController {

    private final FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FriendsDTO> getAllFriends() {
        try {
            return this.friendsService.getAllFriends();
        } catch (Exception e) {
            return new ArrayList<FriendsDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/id={friendsId}")
    @ResponseStatus(HttpStatus.OK)
    public FriendsDTO getFriendsById(@PathVariable("friendsId") int friendsId) {
        try {
            return this.friendsService.getFriendsById(friendsId);
        } catch (Exception e) {
            return new FriendsDTO(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/friend_one={friendOne}")
    @ResponseStatus(HttpStatus.OK)
    public List<FriendsDTO> getFriendsByFriendOne(@PathVariable("friendOne") UUID friendOne) {
        try {
            return this.friendsService.getFriendsByFriendOne(friendOne);
        } catch (Exception e) {
            return new ArrayList<FriendsDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @GetMapping("/friend_two={friendTwo}")
    @ResponseStatus(HttpStatus.OK)
    public List<FriendsDTO> getFriendsByFriendTwo(@PathVariable("friendTwo") UUID friendTwo) {
        try {
            return this.friendsService.getFriendsByFriendTwo(friendTwo);
        } catch (Exception e) {
            return new ArrayList<FriendsDTO>(); //"An internal server error occurred => " + e.getMessage();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewFriendsPair(@RequestBody FriendsDTO friendsDTO) {
        try {
            this.friendsService.saveNewFriendsPair(friendsDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriendsPair(@RequestBody FriendsDTO friendsDTO) {
        try {
            this.friendsService.deleteFriendsPair(friendsDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
