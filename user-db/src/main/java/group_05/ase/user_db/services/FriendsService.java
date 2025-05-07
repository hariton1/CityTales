package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.FriendsEntity;
import group_05.ase.user_db.repositories.FriendsRepository;
import group_05.ase.user_db.restData.FriendsDTO;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FriendsService {

    private final FriendsRepository repository;

    public FriendsService(FriendsRepository repository) {
        this.repository = repository;
    }

    public List<FriendsDTO> getAllFriends() {

        ArrayList<FriendsDTO> friends = new ArrayList<>();
        List<FriendsEntity> tmp = this.repository.findAll();

        for(FriendsEntity friendsPair : tmp) {
            friends.add(new FriendsDTO(friendsPair.getFriendsId(), friendsPair.getFriendOne(),
                    friendsPair.getFriendTwo(), friendsPair.getCreDat()));
        }

        return friends;

    }

    public FriendsDTO getFriendsById(int friendsId) {

        FriendsEntity tmp = this.repository.findByFriendsId(friendsId);

        return new FriendsDTO(tmp.getFriendsId(), tmp.getFriendOne(), tmp.getFriendTwo(), tmp.getCreDat());

    }

    public List<FriendsDTO> getFriendsByFriendOne(UUID friendOne) {

        ArrayList<FriendsDTO> friends = new ArrayList<>();
        List<FriendsEntity> tmp = this.repository.findByFriendOne(friendOne);

        for(FriendsEntity friendsPair : tmp) {
            friends.add(new FriendsDTO(friendsPair.getFriendsId(), friendsPair.getFriendOne(),
                    friendsPair.getFriendTwo(), friendsPair.getCreDat()));
        }

        return friends;

    }

    public List<FriendsDTO> getFriendsByFriendTwo(UUID friendTwo) {

        ArrayList<FriendsDTO> friends = new ArrayList<>();
        List<FriendsEntity> tmp = this.repository.findByFriendTwo(friendTwo);

        for(FriendsEntity friendsPair : tmp) {
            friends.add(new FriendsDTO(friendsPair.getFriendsId(), friendsPair.getFriendOne(),
                    friendsPair.getFriendTwo(), friendsPair.getCreDat()));
        }

        return friends;

    }

    public void saveNewFriendsPair(FriendsDTO friendsDTO) {

        FriendsEntity tmp = new FriendsEntity();

        tmp.setFriendOne(friendsDTO.getFriendOne());
        tmp.setFriendTwo(friendsDTO.getFriendTwo());
        tmp.setCreDat(friendsDTO.getCreDat());

        this.repository.save(tmp);

    }

    public void deleteFriendsPair(FriendsDTO friendsDTO) {

        FriendsEntity tmp = new FriendsEntity();

        tmp.setFriendsId(friendsDTO.getFriendsId());
        tmp.setFriendOne(friendsDTO.getFriendOne());
        tmp.setFriendTwo(friendsDTO.getFriendTwo());
        tmp.setCreDat(friendsDTO.getCreDat());

        this.repository.save(tmp);

    }

}
