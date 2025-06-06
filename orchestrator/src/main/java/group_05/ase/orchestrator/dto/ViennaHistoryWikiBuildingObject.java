package group_05.ase.orchestrator.dto;

import lombok.Data;
import java.util.List;

@Data
public class ViennaHistoryWikiBuildingObject {
    private int viennaHistoryWikiId;
    private String url;
    private String name;
    private String buildingType;      // jetzt KEIN Optional!
    private String dateFrom;
    private String dateTo;
    // ... weitere Felder nach Wunsch
    private Double latitude;
    private Double longitude;
    private List<String> links;
    private List<String> imageUrls;
    // ggf. weitere simple Felder
}