package group_05.ase.neo4j_data_access.DTO;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;

public class HistoricPersonDTO {
    private ViennaHistoryWikiPersonObject person;
    private String content;

    public HistoricPersonDTO(ViennaHistoryWikiPersonObject person, String content) {
        this.person = person;
        this.content = content;
    }

    public ViennaHistoryWikiPersonObject getPerson() {
        return person;
    }

    public void setPerson(ViennaHistoryWikiPersonObject person) {
        this.person = person;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
