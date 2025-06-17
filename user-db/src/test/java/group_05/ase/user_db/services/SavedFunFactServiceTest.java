package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.SavedFunFactEntity;
import group_05.ase.user_db.repositories.SavedFunFactRepository;
import group_05.ase.user_db.restData.SavedFunFactDTO;
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
public class SavedFunFactServiceTest {

    @Mock
    SavedFunFactRepository savedFunFactRepository;

    @InjectMocks
    SavedFunFactService savedFunFactService;

    private final SavedFunFactEntity savedFunFactEntity = new SavedFunFactEntity (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            1,
            "test headline",
            "test funFact",
            "test imageUrl",
            0.01F,
            "test reason",
            null
    );

    private final SavedFunFactDTO savedFunFactDTO = new SavedFunFactDTO (
            1,
            UUID.fromString("f5599c8c-166b-495c-accc-65addfaa572b"),
            1,
            "test headline",
            "test funFact",
            "test imageUrl",
            0.01F,
            "test reason",
            null
    );

    private final ArrayList<SavedFunFactEntity> savedFunFactEntities = new ArrayList<>(List.of(savedFunFactEntity));

    @Test
    public void testGetAllByUserId() {

        when(savedFunFactRepository.findAllByUserId(savedFunFactEntity.getUserId())).thenReturn(savedFunFactEntities);

        ArrayList<SavedFunFactDTO> savedFunFactDTOs = new ArrayList<>(savedFunFactService.getAllByUserId(savedFunFactEntity.getUserId()));

        assertThat(savedFunFactDTOs.getFirst().getSavedFunFactId()).isEqualTo(savedFunFactEntity.getSavedFunFactId());
        assertThat(savedFunFactDTOs.getFirst().getUserId()).isEqualTo(savedFunFactEntity.getUserId());
        assertThat(savedFunFactDTOs.getFirst().getArticleId()).isEqualTo(savedFunFactEntity.getArticleId());
        assertThat(savedFunFactDTOs.getFirst().getHeadline()).isEqualTo(savedFunFactEntity.getHeadline());
        assertThat(savedFunFactDTOs.getFirst().getFunFact()).isEqualTo(savedFunFactEntity.getFunFact());
        assertThat(savedFunFactDTOs.getFirst().getImageUrl()).isEqualTo(savedFunFactEntity.getImageUrl());
        assertThat(savedFunFactDTOs.getFirst().getScore()).isEqualTo(savedFunFactEntity.getScore());
        assertThat(savedFunFactDTOs.getFirst().getReason()).isEqualTo(savedFunFactEntity.getReason());

    }

    @Test
    public void testSaveNewSavedFunFact() {

        when(savedFunFactRepository.save(any(SavedFunFactEntity.class))).thenReturn(savedFunFactEntity);

        SavedFunFactDTO savedFunFactDTO = savedFunFactService.saveNewSavedFunFact(this.savedFunFactDTO);

        assertThat(savedFunFactDTO.getSavedFunFactId()).isEqualTo(this.savedFunFactDTO.getSavedFunFactId());
        assertThat(savedFunFactDTO.getUserId()).isEqualTo(this.savedFunFactDTO.getUserId());
        assertThat(savedFunFactDTO.getArticleId()).isEqualTo(this.savedFunFactDTO.getArticleId());
        assertThat(savedFunFactDTO.getHeadline()).isEqualTo(this.savedFunFactDTO.getHeadline());
        assertThat(savedFunFactDTO.getFunFact()).isEqualTo(this.savedFunFactDTO.getFunFact());
        assertThat(savedFunFactDTO.getImageUrl()).isEqualTo(this.savedFunFactDTO.getImageUrl());
        assertThat(savedFunFactDTO.getScore()).isEqualTo(this.savedFunFactDTO.getScore());
        assertThat(savedFunFactDTO.getReason()).isEqualTo(this.savedFunFactDTO.getReason());

    }

}
