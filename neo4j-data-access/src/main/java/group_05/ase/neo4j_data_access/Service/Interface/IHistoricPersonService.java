package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import java.util.List;

public interface IHistoricPersonService {
    HistoricPersonDTO getPersonById(String wikiDataId);

    List<HistoricPersonDTO> getPersonsByPartialName(String partialName);

    List<HistoricPersonDTO> getAllLinkedHistoricPersonsById(String wikiDataId);

}
