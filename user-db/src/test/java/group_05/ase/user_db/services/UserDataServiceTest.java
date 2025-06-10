package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.UserDataEntity;
import group_05.ase.user_db.repositories.UserDataRepository;
import group_05.ase.user_db.restData.UserDataDTO;
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
public class UserDataServiceTest {

    @Mock
    UserDataRepository userDataRepository;

    @InjectMocks
    UserDataService userDataService;

    private final UserDataEntity userDataEntity = new UserDataEntity (
            -1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            "Admin",
            "Active",
            null
    );

    private final UserDataDTO userDataDto = new UserDataDTO (
            -1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            "Admin",
            "Active",
            null
    );

    private final ArrayList<UserDataEntity> userDataEntities = new ArrayList<>(List.of(userDataEntity));

    @Test
    public void testGetAllUserData() {

        when(userDataRepository.findAll()).thenReturn(userDataEntities);

        ArrayList<UserDataDTO> userDataDTOs = new ArrayList<>(userDataService.getAllUserData());

        assertThat(userDataDTOs.getFirst().getUserId()).isEqualTo(userDataEntity.getUserId());
        assertThat(userDataDTOs.getFirst().getRoleName()).isEqualTo(userDataEntity.getRoleName());
        assertThat(userDataDTOs.getFirst().getStatus()).isEqualTo(userDataEntity.getStatus());

        System.out.println("Test testGetAllUserData passed!");

    }

    @Test
    public void testGetUserDataById() {

        when(userDataRepository.findByUserDataId(any(int.class))).thenReturn(userDataEntity);

        UserDataDTO userDataDTO = userDataService.getUserDataById(userDataEntity.getUserDataId());

        assertThat(userDataDTO.getUserId()).isEqualTo(userDataEntity.getUserId());
        assertThat(userDataDTO.getRoleName()).isEqualTo(userDataEntity.getRoleName());
        assertThat(userDataDTO.getStatus()).isEqualTo(userDataEntity.getStatus());

        System.out.println("Test testGetUserDataById passed!");

    }

    @Test
    public void testGetUserDataByUserId() {

        when(userDataRepository.findByUserId(any(UUID.class))).thenReturn(userDataEntity);

        UserDataDTO userDataDTO = userDataService.getUserDataByUserId(userDataEntity.getUserId());

        assertThat(userDataDTO.getUserId()).isEqualTo(userDataEntity.getUserId());
        assertThat(userDataDTO.getRoleName()).isEqualTo(userDataEntity.getRoleName());
        assertThat(userDataDTO.getStatus()).isEqualTo(userDataEntity.getStatus());

        System.out.println("Test testGetUserDataByUserId passed!");

    }

    @Test
    public void testSaveUserData() {

        when(userDataRepository.save(any(UserDataEntity.class))).thenReturn(userDataEntity);

        UserDataDTO userDataDTO = userDataService.saveUserData(this.userDataDto);

        assertThat(userDataDTO.getUserId()).isEqualTo(this.userDataDto.getUserId());
        assertThat(userDataDTO.getRoleName()).isEqualTo(this.userDataDto.getRoleName());
        assertThat(userDataDTO.getStatus()).isEqualTo(this.userDataDto.getStatus());

        System.out.println("Test testSaveUserData passed.");

    }

}
