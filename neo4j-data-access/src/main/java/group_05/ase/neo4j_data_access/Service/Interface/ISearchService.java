package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricEventDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;

import java.util.List;

public interface ISearchService {

    public List<HistoricPersonDTO> searchPersonsWithKeyword(String keyword);

    public List<HistoricBuildingDTO> searchPlacesWithKeyword(String keyword);

    public List<HistoricEventDTO> searchEventsWithKeyword(String keyword);
}
