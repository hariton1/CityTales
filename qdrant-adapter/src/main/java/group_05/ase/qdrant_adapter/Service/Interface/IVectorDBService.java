package group_05.ase.qdrant_adapter.Service.Interface;

import java.util.List;

public interface IVectorDBService {
    void createCollection(String collectionName);
    void deleteCollection(String collectionName);
    void upsertEntry(List<Float> vector, String collectionName, String wikiDataId);
    List<String> doMatching(List<Float> interests, String collectionName, int resultSize);
}
