package group_05.ase.neo4j_data_access.DTO;

import group_05.ase.neo4j_data_access.Entity.HistoricalPlaceEntity;

public class HistoricPlaceDTO {
    private HistoricalPlaceEntity place;
    private String content;

    public HistoricPlaceDTO(HistoricalPlaceEntity place, String content) {
        this.place = place;
        this.content = content;
    }

    public HistoricalPlaceEntity getPlace() {
        return place;
    }

    public void setPlace(HistoricalPlaceEntity place) {
        this.place = place;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
