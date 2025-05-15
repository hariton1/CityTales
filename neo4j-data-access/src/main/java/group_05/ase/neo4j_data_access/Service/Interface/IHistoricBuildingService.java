package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import java.util.List;

public interface IHistoricBuildingService {
    HistoricBuildingDTO getBuildingById(int viennaHistoryWikiId);

    List<HistoricBuildingDTO> getBuildingByPartialName(String partialName);

    List<HistoricBuildingDTO> findHistoricalBuildingWithinRadius(double latitude, double longitude, double radius);

}
