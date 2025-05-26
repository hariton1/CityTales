package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;

import java.util.List;

public interface IHistoricEventService {
    ViennaHistoryWikiEventObject getEventById(int viennaHistoryWikiId);

    List<ViennaHistoryWikiEventObject> getEventByPartialName(String partialName);

    List<ViennaHistoryWikiEventObject> getAllLinkedHistoricEventsById(int viennaHistoryWikiId);

    List<ViennaHistoryWikiBuildingObject> getAllLinkedHistoricBuildingsById(int viennaHistoryWikiId);

    List<ViennaHistoryWikiPersonObject> getAllLinkedHistoricPersonsById(int viennaHistoryWikiId);
}
