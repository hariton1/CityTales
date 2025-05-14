package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPlaceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import group_05.ase.neo4j_data_access.Service.Interface.ISearchService;

import java.util.List;

@Service
public class SearchService implements ISearchService {

    @Autowired
    private HistoricPlaceService historicPlaceService;
    @Autowired
    private HistoricPersonService historicPersonService;

    @Override
    public List<HistoricPersonDTO> searchPersonsWithKeyword(String query) {

        //TODO: Preprocess Query

        return historicPersonService.getPersonsByPartialName(query);
    }

    @Override
    public List<HistoricPlaceDTO> searchPlacesWithKeyword(String query) {

        //TODO: Preprocess Query

        return historicPlaceService.getPlaceByPartialName(query);
    }
}
