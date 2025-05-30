package group_05.ase.neo4j_data_access.Entity.Tour;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TourObject {

    private String name;
    private String description;
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private List<ViennaHistoryWikiBuildingObject> stops;
    private double distance;
    private double durationEstimate;
    private String userId;

}
