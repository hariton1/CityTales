package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.InterestEntity;
import group_05.ase.user_db.repositories.InterestRepository;
import group_05.ase.user_db.restData.InterestDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterestServiceTest {

    @Mock
    InterestRepository interestRepository;

    @InjectMocks
    InterestService interestService;

    private final InterestEntity interestEntity = new InterestEntity (
            2,
            4,
            "classical",
            "the person is interested in classical music"
    );

    private final ArrayList<InterestEntity> interestEntities = new ArrayList<>(List.of(interestEntity));

    @Test
    public void testGetAllInterests() {

        when(interestRepository.findAll()).thenReturn(interestEntities);

        ArrayList<InterestDTO> interestDTOs = new ArrayList<>(interestService.getAllInterests());

        assertThat(interestDTOs.getFirst().getInterestId()).isEqualTo(interestEntity.getInterestId());
        assertThat(interestDTOs.getFirst().getInterestTypeId()).isEqualTo(interestEntity.getInterestTypeId());
        assertThat(interestDTOs.getFirst().getInterestName()).isEqualTo(interestEntity.getInterestName());
        assertThat(interestDTOs.getFirst().getDescription()).isEqualTo(interestEntity.getDescription());

        System.out.println("Test testGetAllInterests passed!");

    }

    @Test
    public void testGetInterestById() {

        when(interestRepository.findByInterestId(any(int.class))).thenReturn(interestEntity);

        InterestDTO interestDTO = interestService.getInterestById(interestEntity.getInterestId());

        assertThat(interestDTO.getInterestId()).isEqualTo(interestEntity.getInterestId());
        assertThat(interestDTO.getInterestTypeId()).isEqualTo(interestEntity.getInterestTypeId());
        assertThat(interestDTO.getInterestName()).isEqualTo(interestEntity.getInterestName());
        assertThat(interestDTO.getDescription()).isEqualTo(interestEntity.getDescription());

        System.out.println("Test testGetInterestById passed!");

    }

    @Test
    public void testGetInterestByInterestTypeId() {

        when(interestRepository.findByInterestTypeId(any(int.class))).thenReturn(interestEntity);

        InterestDTO interestDTO = interestService.getInterestByInterestTypeId(interestEntity.getInterestTypeId());

        assertThat(interestDTO.getInterestId()).isEqualTo(interestEntity.getInterestId());
        assertThat(interestDTO.getInterestTypeId()).isEqualTo(interestEntity.getInterestTypeId());
        assertThat(interestDTO.getInterestName()).isEqualTo(interestEntity.getInterestName());
        assertThat(interestDTO.getDescription()).isEqualTo(interestEntity.getDescription());

        System.out.println("Test testGetInterestByInterestTypeId passed!");

    }

    @Test
    public void testGetInterestByName() {

        when(interestRepository.findByInterestName(any(String.class))).thenReturn(interestEntity);

        InterestDTO interestDTO = interestService.getInterestByName(interestEntity.getInterestName());

        assertThat(interestDTO.getInterestId()).isEqualTo(interestEntity.getInterestId());
        assertThat(interestDTO.getInterestTypeId()).isEqualTo(interestEntity.getInterestTypeId());
        assertThat(interestDTO.getInterestName()).isEqualTo(interestEntity.getInterestName());
        assertThat(interestDTO.getDescription()).isEqualTo(interestEntity.getDescription());

        System.out.println("Test testGetInterestByName passed!");

    }

    @Test
    public void testGetInterestsByDescriptionLike() {

        when(interestRepository.findByDescriptionContaining(any(String.class))).thenReturn(interestEntities);

        ArrayList<InterestDTO> interestDTOs = new ArrayList<>(interestService.getInterestsByDescriptionLike(interestEntity.getDescription()));

        assertThat(interestDTOs.getFirst().getInterestId()).isEqualTo(interestEntity.getInterestId());
        assertThat(interestDTOs.getFirst().getInterestTypeId()).isEqualTo(interestEntity.getInterestTypeId());
        assertThat(interestDTOs.getFirst().getInterestName()).isEqualTo(interestEntity.getInterestName());
        assertThat(interestDTOs.getFirst().getDescription()).isEqualTo(interestEntity.getDescription());

        System.out.println("Test testGetInterestsByDescriptionLike passed!");

    }

    @Test
    public void testSaveNewInterest() {

        System.out.println("Test testSaveNewInterest not provided.");

    }

}
