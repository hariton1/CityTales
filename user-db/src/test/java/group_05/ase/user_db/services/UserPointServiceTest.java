package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserPointEntity;
import group_05.ase.user_db.repositories.UserPointRepository;
import group_05.ase.user_db.restData.UserPointDTO;
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
public class UserPointServiceTest {

    @Mock
    UserPointRepository userPointRepository;

    @InjectMocks
    UserPointService userPointService;

    private final UserPointEntity userPointEntity = new UserPointEntity (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            1,
            null,
            -1
    );

    private final ArrayList<UserPointEntity> userPointEntities = new ArrayList<>(List.of(userPointEntity));

    @Test
    public void testGetAllUserPoints() {

        when(userPointRepository.findAll()).thenReturn(userPointEntities);

        ArrayList<UserPointDTO> userPointDTOs = new ArrayList<>(userPointService.getAllUserPoints());

        assertThat(userPointDTOs.getFirst().getUserPointId()).isEqualTo(userPointEntity.getUserPointId());
        assertThat(userPointDTOs.getFirst().getUserId()).isEqualTo(userPointEntity.getUserId());
        assertThat(userPointDTOs.getFirst().getPoints()).isEqualTo(userPointEntity.getPoints());
        assertThat(userPointDTOs.getFirst().getArticleId()).isEqualTo(userPointEntity.getArticleId());

        System.out.println("Test testGetAllUserPoints passed!");

    }

    @Test
    public void testGetUserPointById() {

        when(userPointRepository.findByUserPointId(any(int.class))).thenReturn(userPointEntity);

        UserPointDTO userPointDTO = userPointService.getUserPointById(userPointEntity.getUserPointId());

        assertThat(userPointDTO.getUserPointId()).isEqualTo(userPointEntity.getUserPointId());
        assertThat(userPointDTO.getUserId()).isEqualTo(userPointEntity.getUserId());
        assertThat(userPointDTO.getPoints()).isEqualTo(userPointEntity.getPoints());
        assertThat(userPointDTO.getArticleId()).isEqualTo(userPointEntity.getArticleId());

        System.out.println("Test testGetUserPointById passed!");

    }

    @Test
    public void testGetUserPointsByUserId() {

        when(userPointRepository.findByUserId(any(UUID.class))).thenReturn(userPointEntities);

        ArrayList<UserPointDTO> userPointDTOs = new ArrayList<>(userPointService.getUserPointsByUserId(userPointEntity.getUserId()));

        assertThat(userPointDTOs.getFirst().getUserPointId()).isEqualTo(userPointEntity.getUserPointId());
        assertThat(userPointDTOs.getFirst().getUserId()).isEqualTo(userPointEntity.getUserId());
        assertThat(userPointDTOs.getFirst().getPoints()).isEqualTo(userPointEntity.getPoints());
        assertThat(userPointDTOs.getFirst().getArticleId()).isEqualTo(userPointEntity.getArticleId());

        System.out.println("Test testGetUserPointsByUserId passed!");

    }

    @Test
    public void testSaveNewPoints() {

        System.out.println("Test testSaveNewPoints not provided.");

    }

}
