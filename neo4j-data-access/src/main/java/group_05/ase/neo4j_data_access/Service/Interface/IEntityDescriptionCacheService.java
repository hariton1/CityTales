package group_05.ase.neo4j_data_access.Service.Interface;

public interface IEntityDescriptionCacheService {
    public boolean isInCache(String url);

    public String getCachedDescription(String url);

    public void addDescriptionToCache(String url, String description);

    public String extractMainArticleText(String url);

}
