package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.DTO.HistoricPlaceDTO;
import java.util.List;

public interface IHistoricPlaceService {
    HistoricPlaceDTO getPlaceById(String wikiDataId);

    List<HistoricPlaceDTO> getPlaceByPartialName(String partialName);

    List<HistoricPlaceDTO> findHistoricalPlacesWithinRadius(double latitude, double longitude, double radius);

}
