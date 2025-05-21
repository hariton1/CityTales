package group_05.ase.neo4j_data_access.Service.Interface;

public interface IEntityDescriptionCacheService {
    boolean isInCache(String url);

    String getCachedDescription(String url);

    void addDescriptionToCache(String url, String description);

    String extractMainArticleText(String url);

}
