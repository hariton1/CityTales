package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import org.neo4j.driver.types.Node;

public interface IMappingService {

    ViennaHistoryWikiBuildingObject mapNodeToHistoricalBuildingEntity(Node node);

    ViennaHistoryWikiEventObject mapNodeToEventEntity(Node node);

    ViennaHistoryWikiPersonObject mapNodeToPersonEntity(Node node);

}
