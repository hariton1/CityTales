package group_05.ase.qdrant_adapter.Service.Interface;


import java.util.List;

public interface IEmbeddingsService {

    List<Float> getInterestsEmbedding(List<String> interests);
    List<Float> getArticleEmbedding(String content);
}
