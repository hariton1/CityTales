package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.InterestEntity;
import group_05.ase.user_db.entities.UserInterestEntity;
import group_05.ase.user_db.repositories.InterestRepository;
import group_05.ase.user_db.repositories.UserInterestRepository;
import group_05.ase.user_db.restData.UserInterestDTO;
import group_05.ase.user_db.restData.UserInterestWithWeightDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserInterestServiceTest {

    @Mock
    UserInterestRepository userInterestRepository;

    @InjectMocks
    UserInterestService userInterestService;

    @Mock
    private InterestRepository interestRepository;


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

    }

    @Test
    public void testGetUserInterestsByUserId() {

        when(userInterestRepository.findByUserIdOrderByInterestIdAsc(any(UUID.class))).thenReturn(userInterestEntities);

        ArrayList<UserInterestDTO> userInterestDTOs = new ArrayList<>(userInterestService.getUserInterestsByUserId(userInterestEntity.getUserId()));

        assertThat(userInterestDTOs.getFirst().getUserId()).isEqualTo(userInterestEntity.getUserId());
        assertThat(userInterestDTOs.getFirst().getInterestId()).isEqualTo(userInterestEntity.getInterestId());
        assertThat(userInterestDTOs.getFirst().getInterestWeight()).isEqualTo(userInterestEntity.getInterestWeight());

    }

    @Test
    public void testGetUserInterestsByInterestId() {

        when(userInterestRepository.findByInterestIdOrderByInterestIdAsc(any(int.class))).thenReturn(userInterestEntities);

        ArrayList<UserInterestDTO> userInterestDTOs = new ArrayList<>(userInterestService.getUserInterestsByInterestId(userInterestEntity.getInterestId()));

        assertThat(userInterestDTOs.getFirst().getUserId()).isEqualTo(userInterestEntity.getUserId());
        assertThat(userInterestDTOs.getFirst().getInterestId()).isEqualTo(userInterestEntity.getInterestId());
        assertThat(userInterestDTOs.getFirst().getInterestWeight()).isEqualTo(userInterestEntity.getInterestWeight());

    }

    @Test
    public void returnsAllInterestsWithNamesAndWeights() {
        // Setup: baue InterestEntity und UserInterestEntity
        InterestEntity interest = new InterestEntity();
        interest.setInterestId(3); // IDs von Hand setzen!
        interest.setInterestNameEn("music");
        interest.setDescription("desc");
        interest.setInterestNameDe("Musik");

        UserInterestEntity userInterest = new UserInterestEntity();
        UUID userId = UUID.randomUUID();
        userInterest.setUserId(userId);
        userInterest.setInterestId(3);
        userInterest.setInterestWeight(0.8f);

        List<UserInterestEntity> userInterestEntities = List.of(userInterest);

        // Mock: UserInterestRepository liefert unsere UserInterests
        when(userInterestRepository.findByUserIdOrderByInterestIdAsc(userId)).thenReturn(userInterestEntities);

        // Mock: InterestRepository liefert das passende InterestEntity
        when(interestRepository.findById(3)).thenReturn(java.util.Optional.of(interest));

        // Exercise
        List<UserInterestWithWeightDTO> result = userInterestService.getUserInterestsWithWeightByUserId(userId);

        // Verify
        assertEquals(1, result.size());
        UserInterestWithWeightDTO dto = result.get(0);
        assertEquals("music", dto.getInterestNameEn());
        assertEquals(0.8f, dto.getInterestWeight());
    }


}
