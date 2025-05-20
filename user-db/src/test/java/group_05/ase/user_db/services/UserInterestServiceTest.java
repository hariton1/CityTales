package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserInterestEntity;
import group_05.ase.user_db.repositories.UserInterestRepository;
import group_05.ase.user_db.restData.UserInterestDTO;
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
public class UserInterestServiceTest {

    @Mock
    UserInterestRepository userInterestRepository;

    @InjectMocks
    UserInterestService userInterestService;

    private final UserInterestEntity userInterestEntity = new UserInterestEntity (
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            3,
            null,
            1
    );

    private final ArrayList<UserInterestEntity> userInterestEntities = new ArrayList<>(List.of(userInterestEntity));

    @Test
    public void testGetAllUserInterests() {

        when(userInterestRepository.findAll()).thenReturn(userInterestEntities);

        ArrayList<UserInterestDTO> userInterestDTOs = new ArrayList<>(userInterestService.getAllUserInterests());

        assertThat(userInterestDTOs.getFirst().getUserId()).isEqualTo(userInterestEntity.getUserId());
        assertThat(userInterestDTOs.getFirst().getInterestId()).isEqualTo(userInterestEntity.getInterestId());
        assertThat(userInterestDTOs.getFirst().getInterestWeight()).isEqualTo(userInterestEntity.getInterestWeight());

        System.out.println("Test testGetAllUserInterests passed!");

    }

    @Test
    public void testGetUserInterestsByUserId() {

        when(userInterestRepository.findByUserId(any(UUID.class))).thenReturn(userInterestEntities);

        ArrayList<UserInterestDTO> userInterestDTOs = new ArrayList<>(userInterestService.getUserInterestsByUserId(userInterestEntity.getUserId()));

        assertThat(userInterestDTOs.getFirst().getUserId()).isEqualTo(userInterestEntity.getUserId());
        assertThat(userInterestDTOs.getFirst().getInterestId()).isEqualTo(userInterestEntity.getInterestId());
        assertThat(userInterestDTOs.getFirst().getInterestWeight()).isEqualTo(userInterestEntity.getInterestWeight());

        System.out.println("Test testGetUserInterestsByUserId passed!");

    }

    @Test
    public void testGetUserInterestsByInterestId() {

        when(userInterestRepository.findByInterestId(any(int.class))).thenReturn(userInterestEntities);

        ArrayList<UserInterestDTO> userInterestDTOs = new ArrayList<>(userInterestService.getUserInterestsByInterestId(userInterestEntity.getInterestId()));

        assertThat(userInterestDTOs.getFirst().getUserId()).isEqualTo(userInterestEntity.getUserId());
        assertThat(userInterestDTOs.getFirst().getInterestId()).isEqualTo(userInterestEntity.getInterestId());
        assertThat(userInterestDTOs.getFirst().getInterestWeight()).isEqualTo(userInterestEntity.getInterestWeight());

        System.out.println("Test testGetUserInterestsByInterestId passed!");

    }

    @Test
    public void testSaveNewUserInterest() {

        System.out.println("Test testSaveNewUserInterest not provided.");

    }

    @Test
    public void testDeleteUserInterest() {

        System.out.println("Test testDeleteUserInterest not provided.");

    }

}
