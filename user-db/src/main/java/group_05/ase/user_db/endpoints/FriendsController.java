package group_05.ase.user_db.endpoints;

import group_05.ase.user_db.restData.FriendsDTO;
import group_05.ase.user_db.services.FriendsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/friends")
public class FriendsController {

    private static final Logger logger = LoggerFactory.getLogger(FriendsController.class);

    private final FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FriendsDTO> getAllFriends() {
        try {
            logger.info("Fetching all friends");
            return this.friendsService.getAllFriends();
        } catch (Exception e) {
            logger.error("Error fetching friends: {}", e.getMessage());
            throw new RuntimeException("Error fetching friends", e);
        }
    }

    @GetMapping("/id={friendsId}")
    @ResponseStatus(HttpStatus.OK)
    public FriendsDTO getFriendsById(@PathVariable("friendsId") int friendsId) {
        try {
            logger.info("Fetching friends by id: {}", friendsId);
            return this.friendsService.getFriendsById(friendsId);
        } catch (Exception e) {
            logger.error("Error fetching friends by id {}: {}", friendsId, e.getMessage());
            throw new RuntimeException("Error fetching friends", e);
        }
    }

    @GetMapping("/friend_one={friendOne}")
    @ResponseStatus(HttpStatus.OK)
    public List<FriendsDTO> getFriendsByFriendOne(@PathVariable("friendOne") UUID friendOne) {
        try {
            logger.info("Fetching friends by fiend_one_id: {}", friendOne.toString());
            return this.friendsService.getFriendsByFriendOne(friendOne);
        } catch (Exception e) {
            logger.error("Error fetching friends by fiend_one_id {}: {}", friendOne.toString(), e.getMessage());
            throw new RuntimeException("Error fetching friends", e);
        }
    }

    @GetMapping("/friend_two={friendTwo}")
    @ResponseStatus(HttpStatus.OK)
    public List<FriendsDTO> getFriendsByFriendTwo(@PathVariable("friendTwo") UUID friendTwo) {
        try {
            logger.info("Fetching friends by fiend_two_id: {}", friendTwo.toString());
            return this.friendsService.getFriendsByFriendTwo(friendTwo);
        } catch (Exception e) {
            logger.error("Error fetching friends by fiend_two_id {}: {}", friendTwo.toString(), e.getMessage());
            throw new RuntimeException("Error fetching friends", e);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewFriendsPair(@RequestBody FriendsDTO friendsDTO) {
        try {
            logger.info("Creating friends: {}", friendsDTO.toString());
            this.friendsService.saveNewFriendsPair(friendsDTO);
        } catch (Exception e) {
            logger.error("Error creating friends {}: {}", friendsDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating friends", e);
        }
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriendsPair(@RequestBody FriendsDTO friendsDTO) {
        try {
            logger.info("Deleting friends: {}", friendsDTO.toString());
            this.friendsService.deleteFriendsPair(friendsDTO);
        } catch (Exception e) {
            logger.error("Error deleting friends {}: {}", friendsDTO.toString(), e.getMessage());
            throw new RuntimeException("Error deleting friends", e);
        }
    }
}
