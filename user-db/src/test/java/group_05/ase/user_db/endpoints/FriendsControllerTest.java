package group_05.ase.user_db.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.user_db.restData.FriendsDTO;
import group_05.ase.user_db.services.FriendsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class FriendsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    FriendsService friendsService;

    private final FriendsDTO friendsDTO = new FriendsDTO (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            null
    );

    private final ArrayList<FriendsDTO> friendsDTOs = new ArrayList<>(List.of(friendsDTO));

    @Test
    public void testGetAllFriends() throws Exception {

        when(friendsService.getAllFriends()).thenReturn(friendsDTOs);

        mockMvc.perform(get("/friends")
                        .content(mapper.writeValueAsString(friendsDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].friends_id").value(friendsDTO.getFriendsId()))
                .andExpect(jsonPath("$[0].friend_one").value(friendsDTO.getFriendOne().toString()))
                .andExpect(jsonPath("$[0].friend_two").value(friendsDTO.getFriendTwo().toString()));

        System.out.println("Test testGetAllFeedbacks passed!");

    }

    @Test
    public void testGetFriendsById() throws Exception {

        when(friendsService.getFriendsById(any(int.class))).thenReturn(friendsDTO);

        mockMvc.perform(get("/friends/id=1")
                        .content(mapper.writeValueAsString(friendsDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friends_id").value(friendsDTO.getFriendsId()))
                .andExpect(jsonPath("$.friend_one").value(friendsDTO.getFriendOne().toString()))
                .andExpect(jsonPath("$.friend_two").value(friendsDTO.getFriendTwo().toString()));

        System.out.println("Test testGetFriendsById passed!");

    }

    @Test
    public void testGetFriendsByFriendOne() throws Exception {

        when(friendsService.getFriendsByFriendOne(any(UUID.class))).thenReturn(friendsDTOs);

        mockMvc.perform(get("/friends/friend_one=f5599c8c-166b-495c-accc-65addfaa572b")
                        .content(mapper.writeValueAsString(friendsDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].friends_id").value(friendsDTO.getFriendsId()))
                .andExpect(jsonPath("$[0].friend_one").value(friendsDTO.getFriendOne().toString()))
                .andExpect(jsonPath("$[0].friend_two").value(friendsDTO.getFriendTwo().toString()));

        System.out.println("Test testGetFriendsByFriendOne passed!");

    }

    @Test
    public void testGetFriendsByFriendTwo() throws Exception {

        when(friendsService.getFriendsByFriendTwo(any(UUID.class))).thenReturn(friendsDTOs);

        mockMvc.perform(get("/friends/friend_two=f5599c8c-166b-495c-accc-65addfaa572b")
                        .content(mapper.writeValueAsString(friendsDTOs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].friends_id").value(friendsDTO.getFriendsId()))
                .andExpect(jsonPath("$[0].friend_one").value(friendsDTO.getFriendOne().toString()))
                .andExpect(jsonPath("$[0].friend_two").value(friendsDTO.getFriendTwo().toString()));

        System.out.println("Test testGetFriendsByFriendTwo passed!");

    }

    @Test
    public void testCreateNewFriendsPair() throws Exception {

        System.out.println("Test testCreateNewFriendsPair not provided!");

    }

    @Test
    public void testDeleteFriendsPair() throws Exception {

        System.out.println("Test testDeleteFriendsPair not provided!");

    }

}
