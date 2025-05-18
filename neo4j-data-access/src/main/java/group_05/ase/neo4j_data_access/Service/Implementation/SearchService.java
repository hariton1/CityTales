package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
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

    @Override
    public List<HistoricPersonDTO> searchPersonsWithKeyword(String query) {

        //TODO: Preprocess Query
        System.out.println(historicPersonService.getPersonsByPartialName(query));
        return historicPersonService.getPersonsByPartialName(query);
    }

    @Override
    public List<HistoricBuildingDTO> searchPlacesWithKeyword(String query) {

        //TODO: Preprocess Query

        return historicBuildingService.getBuildingByPartialName(query);
    }
}
