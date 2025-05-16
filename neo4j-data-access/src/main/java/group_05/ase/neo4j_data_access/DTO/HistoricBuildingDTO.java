package group_05.ase.neo4j_data_access.DTO;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;

public class HistoricBuildingDTO {
    private ViennaHistoryWikiBuildingObject building;
    private String content;

    public HistoricBuildingDTO(ViennaHistoryWikiBuildingObject place, String content) {
        this.building = place;
        this.content = content;
    }

    public ViennaHistoryWikiBuildingObject getBuilding() {
        return building;
    }

    public void setBuilding(ViennaHistoryWikiBuildingObject building) {
        this.building = building;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
