package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService implements ISearchService {

    @Autowired
    private HistoricBuildingService historicBuildingService;
    @Autowired
    private HistoricPersonService historicPersonService;
    @Autowired
    private HistoricEventService historicEventService;

    @Override
    public List<ViennaHistoryWikiPersonObject> searchPersonsWithKeyword(String query) {

        //TODO: Preprocess Query
        System.out.println(historicPersonService.getPersonsByPartialName(query));
        return historicPersonService.getPersonsByPartialName(query);
    }

    @Override
    public List<ViennaHistoryWikiBuildingObject> searchPlacesWithKeyword(String query) {

        //TODO: Preprocess Query

        return historicBuildingService.getBuildingByPartialName(query);
    }

    @Override
    public List<ViennaHistoryWikiEventObject> searchEventsWithKeyword(String query) {
        //TODO: Preprocess Query

        return historicEventService.getEventByPartialName(query);
    }
}
