package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserBadgeEntity;
import group_05.ase.user_db.repositories.UserBadgeRepository;
import group_05.ase.user_db.restData.UserBadgeDTO;
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
public class UserBadgeServiceTest {

    @Mock
    UserBadgeRepository userBadgeRepository;

    @InjectMocks
    UserBadgeService userBadgeService;

    private final UserBadgeEntity userBadgeEntity = new UserBadgeEntity (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            10000,
            null
    );

    private final UserBadgeDTO userBadgeDTO = new UserBadgeDTO (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            10000,
            null
    );

    private final ArrayList<UserBadgeEntity> userBadgeEntities = new ArrayList<>(List.of(userBadgeEntity));

    @Test
    public void testGetAllUserBadges() {

        when(userBadgeRepository.findAll()).thenReturn(userBadgeEntities);

        ArrayList<UserBadgeDTO> userBadgeDTOs = new ArrayList<>(userBadgeService.getAllUserBadges());

        assertThat(userBadgeDTOs.getFirst().getUserBadgeId()).isEqualTo(userBadgeEntity.getUserBadgeId());
        assertThat(userBadgeDTOs.getFirst().getUserId()).isEqualTo(userBadgeEntity.getUserId());
        assertThat(userBadgeDTOs.getFirst().getArticleId()).isEqualTo(userBadgeEntity.getArticleId());

        System.out.println("Test testGetAllUserBadges passed!");

    }

    @Test
    public void testGetUserBadgesByUserId() {

        when(userBadgeRepository.findAllByUserIdOrderByUserBadgeIdAsc(any(UUID.class))).thenReturn(userBadgeEntities);

        ArrayList<UserBadgeDTO> userBadgeDTOs = new ArrayList<>(userBadgeService.getUserBadgesByUserId(userBadgeEntity.getUserId()));

        assertThat(userBadgeDTOs.getFirst().getUserBadgeId()).isEqualTo(userBadgeEntity.getUserBadgeId());
        assertThat(userBadgeDTOs.getFirst().getUserId()).isEqualTo(userBadgeEntity.getUserId());
        assertThat(userBadgeDTOs.getFirst().getArticleId()).isEqualTo(userBadgeEntity.getArticleId());

        System.out.println("Test testGetUserBadgesByUserId passed!");

    }

    @Test
    public void testGetUserBadgesByArticleId() {

        when(userBadgeRepository.findAllByArticleIdOrderByUserBadgeIdAsc(any(int.class))).thenReturn(userBadgeEntities);

        ArrayList<UserBadgeDTO> userBadgeDTOs = new ArrayList<>(userBadgeService.getUserBadgesByArticleId(userBadgeEntity.getArticleId()));

        assertThat(userBadgeDTOs.getFirst().getUserBadgeId()).isEqualTo(userBadgeEntity.getUserBadgeId());
        assertThat(userBadgeDTOs.getFirst().getUserId()).isEqualTo(userBadgeEntity.getUserId());
        assertThat(userBadgeDTOs.getFirst().getArticleId()).isEqualTo(userBadgeEntity.getArticleId());

        System.out.println("Test testGetUserBadgesByUserId passed!");

    }

    @Test
    public void testSaveNewBadge() {

        when(userBadgeRepository.save(any(UserBadgeEntity.class))).thenReturn(userBadgeEntity);

        UserBadgeDTO userBadgeDTO = userBadgeService.saveNewBadge(this.userBadgeDTO);

        assertThat(userBadgeDTO.getUserBadgeId()).isEqualTo(this.userBadgeDTO.getUserBadgeId());
        assertThat(userBadgeDTO.getUserId()).isEqualTo(this.userBadgeDTO.getUserId());
        assertThat(userBadgeDTO.getArticleId()).isEqualTo(this.userBadgeDTO.getArticleId());

        System.out.println("Test testSaveNewBadge passed.");

    }
}
