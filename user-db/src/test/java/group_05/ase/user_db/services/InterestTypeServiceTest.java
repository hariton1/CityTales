package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.InterestTypeEntity;
import group_05.ase.user_db.repositories.InterestTypeRepository;
import group_05.ase.user_db.restData.InterestTypeDTO;
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
public class InterestTypeServiceTest {

    @Mock
    InterestTypeRepository interestTypeRepository;

    @InjectMocks
    InterestTypeService interestTypeService;

    private final InterestTypeEntity interestTypeEntity = new InterestTypeEntity (
            1,
            "medic",
            "the person is interested in medical fun-facts",
            null
    );

    private final ArrayList<InterestTypeEntity> interestTypeEntities = new ArrayList<>(List.of(interestTypeEntity));

    @Test
    public void testGetAllInterestTypes() {

        when(interestTypeRepository.findAll()).thenReturn(interestTypeEntities);

        ArrayList<InterestTypeDTO> interestTypeDTOs = new ArrayList<>(interestTypeService.getAllInterestTypes());

        assertThat(interestTypeDTOs.getFirst().getInterestTypeId()).isEqualTo(interestTypeEntity.getInterestTypeId());
        assertThat(interestTypeDTOs.getFirst().getTypeName()).isEqualTo(interestTypeEntity.getTypeName());
        assertThat(interestTypeDTOs.getFirst().getDescription()).isEqualTo(interestTypeEntity.getDescription());

        System.out.println("Test testGetAllInterestTypes passed!");

    }

    @Test
    public void testGetInterestById() {

        when(interestTypeRepository.findByInterestTypeId(any(int.class))).thenReturn(interestTypeEntity);

        InterestTypeDTO interestTypeDTO = interestTypeService.getInterestById(interestTypeEntity.getInterestTypeId());

        assertThat(interestTypeDTO.getInterestTypeId()).isEqualTo(interestTypeEntity.getInterestTypeId());
        assertThat(interestTypeDTO.getTypeName()).isEqualTo(interestTypeEntity.getTypeName());
        assertThat(interestTypeDTO.getDescription()).isEqualTo(interestTypeEntity.getDescription());

        System.out.println("Test testGetInterestById passed!");

    }

    @Test
    public void testGetInterestByName() {

        when(interestTypeRepository.findByTypeName(any(String.class))).thenReturn(interestTypeEntity);

        InterestTypeDTO interestTypeDTO = interestTypeService.getInterestByName(interestTypeEntity.getTypeName());

        assertThat(interestTypeDTO.getInterestTypeId()).isEqualTo(interestTypeEntity.getInterestTypeId());
        assertThat(interestTypeDTO.getTypeName()).isEqualTo(interestTypeEntity.getTypeName());
        assertThat(interestTypeDTO.getDescription()).isEqualTo(interestTypeEntity.getDescription());

        System.out.println("Test testGetInterestByName passed!");

    }

    @Test
    public void testGetInterestTypesByDescriptionLike() {

        when(interestTypeRepository.findByDescriptionContaining(any(String.class))).thenReturn(interestTypeEntities);

        ArrayList<InterestTypeDTO> interestTypeDTOs = new ArrayList<>(interestTypeService.getInterestTypesByDescriptionLike(interestTypeEntity.getDescription()));

        assertThat(interestTypeDTOs.getFirst().getInterestTypeId()).isEqualTo(interestTypeEntity.getInterestTypeId());
        assertThat(interestTypeDTOs.getFirst().getTypeName()).isEqualTo(interestTypeEntity.getTypeName());
        assertThat(interestTypeDTOs.getFirst().getDescription()).isEqualTo(interestTypeEntity.getDescription());

        System.out.println("Test testGetInterestTypesByDescriptionLike passed!");

    }

    @Test
    public void testSaveNewInterestType() {

        System.out.println("Test testSaveNewInterestType not provided.");

    }

}
