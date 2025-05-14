package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPlaceDTO;

import java.util.List;

public interface ISearchService {

    public List<HistoricPersonDTO> searchPersonsWithKeyword(String keyword);

    public List<HistoricPlaceDTO> searchPlacesWithKeyword(String keyword);
}
