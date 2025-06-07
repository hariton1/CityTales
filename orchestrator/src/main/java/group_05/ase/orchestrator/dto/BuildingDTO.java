package group_05.ase.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingDTO {
    private int viennaHistoryWikiId;
    private String name;
    private String url;
    private String buildingType;
    private Double latitude;
    private Double longitude;
    private float weight;

}
