package group_05.ase.neo4j_data_access.Entity.Tour;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateTourRequestDTO {
    private String userId;
    private double start_lat;
    private double start_lng;
    private double end_lat;
    private double end_lng;
    private List<ViennaHistoryWikiBuildingObject> predefinedStops;
    private double maxDistance;
    private double minDistance;
    private double maxDuration;
    private double minDuration;
    private double maxBudget;
    private Integer numStops;
    private int[] personConfiguration;
}
