package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;

import java.util.List;

public interface ISearchService {

    public List<ViennaHistoryWikiPersonObject> searchPersonsWithKeyword(String keyword);

    public List<ViennaHistoryWikiBuildingObject> searchPlacesWithKeyword(String keyword);

    public List<ViennaHistoryWikiEventObject> searchEventsWithKeyword(String keyword);
}
