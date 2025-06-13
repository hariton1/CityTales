package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.SavedFunFactEntity;
import group_05.ase.user_db.repositories.SavedFunFactRepository;
import group_05.ase.user_db.restData.SavedFunFactDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SavedFunFactService {

    private final SavedFunFactRepository repository;

    public SavedFunFactService(SavedFunFactRepository repository) {
        this.repository = repository;
    }

    public List<SavedFunFactDTO> getAllByUserId(UUID userId) {

        ArrayList<SavedFunFactDTO> savedFunFacts = new ArrayList<>();
        List<SavedFunFactEntity> tmp = this.repository.findAllByUserId(userId);

        for(SavedFunFactEntity savedFunFact : tmp) {
            savedFunFacts.add(new SavedFunFactDTO(
                    savedFunFact.getSavedFunFactId(),
                    savedFunFact.getUserId(),
                    savedFunFact.getArticleId(),
                    savedFunFact.getHeadline(),
                    savedFunFact.getFunFact(),
                    savedFunFact.getImageUrl(),
                    savedFunFact.getScore(),
                    savedFunFact.getReason(),
                    savedFunFact.getSavedAt()
            ));
        }

        return savedFunFacts;

    }

    public SavedFunFactDTO saveNewSavedFunFact(SavedFunFactDTO savedFunFactDTO) {

        SavedFunFactEntity tmp = new SavedFunFactEntity();

        tmp.setUserId(savedFunFactDTO.getUserId());
        tmp.setArticleId(savedFunFactDTO.getArticleId());
        tmp.setHeadline(savedFunFactDTO.getHeadline());
        tmp.setFunFact(savedFunFactDTO.getFunFact());
        tmp.setImageUrl(savedFunFactDTO.getImageUrl());
        tmp.setScore(savedFunFactDTO.getScore());
        tmp.setReason(savedFunFactDTO.getReason());
        tmp.setSavedAt(savedFunFactDTO.getSavedAt());

        SavedFunFactEntity insertedSavedFunFact = this.repository.save(tmp);

        return new SavedFunFactDTO(
                insertedSavedFunFact.getSavedFunFactId(),
                insertedSavedFunFact.getUserId(),
                insertedSavedFunFact.getArticleId(),
                insertedSavedFunFact.getHeadline(),
                insertedSavedFunFact.getFunFact(),
                insertedSavedFunFact.getImageUrl(),
                insertedSavedFunFact.getScore(),
                insertedSavedFunFact.getReason(),
                insertedSavedFunFact.getSavedAt()
        );

    }

    public void deleteSavedFunFact(SavedFunFactDTO savedFunFactDTO) {
        System.out.println("Deleting saved fun fact: " + savedFunFactDTO.toString());
        this.repository.deleteById(savedFunFactDTO.getSavedFunFactId());
    }

}
