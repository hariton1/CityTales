package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.DTO.HistoricBuildingDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricEventDTO;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;

import java.util.List;

public interface IHistoricPersonService {
    HistoricPersonDTO getPersonById(int viennaHistoryWikiId);

    List<HistoricPersonDTO> getPersonsByPartialName(String partialName);

    List<HistoricEventDTO> getAllLinkedHistoricEventsById(int viennaHistoryWikiId);

    List<HistoricBuildingDTO> getAllLinkedHistoricBuildingsById(int viennaHistoryWikiId);

    List<HistoricPersonDTO> getAllLinkedHistoricPersonsById(int viennaHistoryWikiId);

}
