package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserHistoryEntity;
import group_05.ase.user_db.entities.UserInterestEntity;
import group_05.ase.user_db.repositories.UserHistoryRepository;
import group_05.ase.user_db.repositories.UserInterestRepository;
import group_05.ase.user_db.restData.UserHistoryDTO;
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
public class UserHistoryServiceTest {

    @Mock
    UserHistoryRepository userHistoryRepository;
    @Mock
    UserInterestRepository userInterestRepository;

    @InjectMocks
    UserHistoryService userHistoryService;

    private final UserHistoryEntity userHistoryEntity = new UserHistoryEntity (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            1,
            null,
            null,
            2
    );

    private final UserHistoryDTO userHistoryDTO = new UserHistoryDTO (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            1,
            null,
            null,
            2
    );

    private final ArrayList<UserHistoryEntity> userHistoryEntities = new ArrayList<>(List.of(userHistoryEntity));

    @Test
    public void testGetAllUserHistories() {

        when(userHistoryRepository.findAll()).thenReturn(userHistoryEntities);

        ArrayList<UserHistoryDTO> userHistoryDTOs = new ArrayList<>(userHistoryService.getAllUserHistories());

        assertThat(userHistoryDTOs.getFirst().getUserHistoryId()).isEqualTo(userHistoryEntity.getUserHistoryId());
        assertThat(userHistoryDTOs.getFirst().getUserId()).isEqualTo(userHistoryEntity.getUserId());
        assertThat(userHistoryDTOs.getFirst().getArticleId()).isEqualTo(userHistoryEntity.getArticleId());
        assertThat(userHistoryDTOs.getFirst().getInterestId()).isEqualTo(userHistoryEntity.getInterestId());

        System.out.println("Test testGetAllUserHistories passed!");

    }

    @Test
    public void testGetUserHistoriesById() {

        when(userHistoryRepository.findByUserHistoryId(any(int.class))).thenReturn(userHistoryEntity);

        UserHistoryDTO userHistoryDTO = userHistoryService.getUserHistoriesById(userHistoryEntity.getUserHistoryId());

        assertThat(userHistoryDTO.getUserHistoryId()).isEqualTo(userHistoryEntity.getUserHistoryId());
        assertThat(userHistoryDTO.getUserId()).isEqualTo(userHistoryEntity.getUserId());
        assertThat(userHistoryDTO.getArticleId()).isEqualTo(userHistoryEntity.getArticleId());
        assertThat(userHistoryDTO.getInterestId()).isEqualTo(userHistoryEntity.getInterestId());

        System.out.println("Test testGetUserHistoriesById passed!");

    }

    @Test
    public void testGetUserHistoriesByUserId() {

        when(userHistoryRepository.findAllByUserId(any(UUID.class))).thenReturn(userHistoryEntities);

        ArrayList<UserHistoryDTO> userHistoryDTOs = new ArrayList<>(userHistoryService.getUserHistoriesByUserId(userHistoryEntity.getUserId()));

        assertThat(userHistoryDTOs.getFirst().getUserHistoryId()).isEqualTo(userHistoryEntity.getUserHistoryId());
        assertThat(userHistoryDTOs.getFirst().getUserId()).isEqualTo(userHistoryEntity.getUserId());
        assertThat(userHistoryDTOs.getFirst().getArticleId()).isEqualTo(userHistoryEntity.getArticleId());
        assertThat(userHistoryDTOs.getFirst().getInterestId()).isEqualTo(userHistoryEntity.getInterestId());

        System.out.println("Test testGetUserHistoriesByUserId passed!");

    }

    @Test
    public void testGetUserHistoriesByArticleId() {

        when(userHistoryRepository.findAllByArticleId(any(int.class))).thenReturn(userHistoryEntities);

        ArrayList<UserHistoryDTO> userHistoryDTOs = new ArrayList<>(userHistoryService.getUserHistoriesByArticleId(userHistoryEntity.getArticleId()));

        assertThat(userHistoryDTOs.getFirst().getUserHistoryId()).isEqualTo(userHistoryEntity.getUserHistoryId());
        assertThat(userHistoryDTOs.getFirst().getUserId()).isEqualTo(userHistoryEntity.getUserId());
        assertThat(userHistoryDTOs.getFirst().getArticleId()).isEqualTo(userHistoryEntity.getArticleId());
        assertThat(userHistoryDTOs.getFirst().getInterestId()).isEqualTo(userHistoryEntity.getInterestId());

        System.out.println("Test testGetUserHistoriesByArticleId passed!");

    }

    @Test
    public void testSaveNewUserHistory() {

        when(userHistoryRepository.save(any(UserHistoryEntity.class))).thenReturn(userHistoryEntity);
        when(userInterestRepository.findByUserIdAndInterestId(any(UUID.class), any(int.class))).thenReturn(new UserInterestEntity());

        UserHistoryDTO userHistoryDTO = userHistoryService.saveNewUserHistory(this.userHistoryDTO);

        assertThat(userHistoryDTO.getUserHistoryId()).isEqualTo(userHistoryEntity.getUserHistoryId());
        assertThat(userHistoryDTO.getUserId()).isEqualTo(userHistoryEntity.getUserId());
        assertThat(userHistoryDTO.getArticleId()).isEqualTo(userHistoryEntity.getArticleId());
        assertThat(userHistoryDTO.getInterestId()).isEqualTo(userHistoryEntity.getInterestId());

        System.out.println("Test testSaveNewUserHistory passed.");

    }

    @Test
    public void testSaveChangedUserHistory() {

        System.out.println("Test testSaveChangedUserHistory not provided.");

    }

}
