package group_05.ase.neo4j_data_access.Entity.Tour;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DurationDistanceEstimateDTO {
    private double start_lat;
    private double start_lng;
    private double end_lat;
    private double end_lng;
    private List<ViennaHistoryWikiBuildingObject> stops;
}
