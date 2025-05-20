package group_05.ase.neo4j_data_access.DTO;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;

public class HistoricEventDTO {

    public HistoricEventDTO(ViennaHistoryWikiEventObject event, String content) {
        this.content= content;
        this.event = event;
    }

    private String content;
    private ViennaHistoryWikiEventObject event;

    public ViennaHistoryWikiEventObject getEvent() {
        return event;
    }

    public void setEvent(ViennaHistoryWikiEventObject event) {
        this.event = event;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
