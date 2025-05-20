package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricEventDTO;

import java.util.List;

public interface IHistoricEventService {
    HistoricEventDTO getEventById(int viennaHistoryWikiId);

    List<HistoricEventDTO> getEventByPartialName(String partialName);

    List<HistoricEventDTO> getAllLinkedHistoricEventsById(int viennaHistoryWikiId);
}
