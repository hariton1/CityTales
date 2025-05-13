package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.FriendsEntity;
import group_05.ase.user_db.repositories.FriendsRepository;
import group_05.ase.user_db.restData.FriendsDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FriendsServiceTest {

    @Mock
    FriendsRepository friendsRepository;

    @InjectMocks
    FriendsService friendsService;

    private final FriendsEntity friendsEntity = new FriendsEntity (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572a"),
            null
    );

    private final ArrayList<FriendsEntity> friendsEntities = new ArrayList<>(List.of(friendsEntity));

    @Test
    public void testGetAllFriends() {

        when(friendsRepository.findAll()).thenReturn(friendsEntities);

        ArrayList<FriendsDTO> friendsDTOs = new ArrayList<>(friendsService.getAllFriends());

        assertThat(friendsDTOs.getFirst().getFriendsId()).isEqualTo(friendsEntity.getFriendsId());
        assertThat(friendsDTOs.getFirst().getFriendOne()).isEqualTo(friendsEntity.getFriendOne());
        assertThat(friendsDTOs.getFirst().getFriendTwo()).isEqualTo(friendsEntity.getFriendTwo());

        System.out.println("Test testGetAllFriends passed!");

    }

    @Test
    public void testGetFriendsById() {

        when(friendsRepository.findByFriendsId(any(int.class))).thenReturn(friendsEntity);

        FriendsDTO friendsDTO = friendsService.getFriendsById(friendsEntity.getFriendsId());

        assertThat(friendsDTO.getFriendsId()).isEqualTo(friendsEntity.getFriendsId());
        assertThat(friendsDTO.getFriendOne()).isEqualTo(friendsEntity.getFriendOne());
        assertThat(friendsDTO.getFriendTwo()).isEqualTo(friendsEntity.getFriendTwo());

        System.out.println("Test testGetFriendsById passed!");

    }

    @Test
    public void testGetFriendsByFriendOne() {

        when(friendsRepository.findByFriendOne(any(UUID.class))).thenReturn(friendsEntities);

        ArrayList<FriendsDTO> friendsDTOs = new ArrayList<>(friendsService.getFriendsByFriendOne(friendsEntity.getFriendOne()));

        assertThat(friendsDTOs.getFirst().getFriendsId()).isEqualTo(friendsEntity.getFriendsId());
        assertThat(friendsDTOs.getFirst().getFriendOne()).isEqualTo(friendsEntity.getFriendOne());
        assertThat(friendsDTOs.getFirst().getFriendTwo()).isEqualTo(friendsEntity.getFriendTwo());

        System.out.println("Test testGetFriendsByFriendOne passed!");

    }

    @Test
    public void testGetFriendsByFriendTwo() {

        when(friendsRepository.findByFriendTwo(any(UUID.class))).thenReturn(friendsEntities);

        ArrayList<FriendsDTO> friendsDTOs = new ArrayList<>(friendsService.getFriendsByFriendTwo(friendsEntity.getFriendTwo()));

        assertThat(friendsDTOs.getFirst().getFriendsId()).isEqualTo(friendsEntity.getFriendsId());
        assertThat(friendsDTOs.getFirst().getFriendOne()).isEqualTo(friendsEntity.getFriendOne());
        assertThat(friendsDTOs.getFirst().getFriendTwo()).isEqualTo(friendsEntity.getFriendTwo());

        System.out.println("Test testGetFriendsByFriendTwo passed!");

    }

    @Test
    public void testSaveNewFriendsPair() {

        System.out.println("Test testSaveNewFriendsPair not provided.");

    }

    @Test
    public void testDeleteFriendsPair() {

        System.out.println("Test testDeleteFriendsPair not provided.");

    }

}
