package group_05.ase.neo4j_data_access.DTO;

import group_05.ase.neo4j_data_access.Entity.HistoricalPersonEntity;

public class HistoricPersonDTO {
    private HistoricalPersonEntity person;
    private String content;

    public HistoricPersonDTO(HistoricalPersonEntity person, String content) {
        this.person = person;
        this.content = content;
    }

    public HistoricalPersonEntity getPerson() {
        return person;
    }

    public void setPerson(HistoricalPersonEntity person) {
        this.person = person;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
