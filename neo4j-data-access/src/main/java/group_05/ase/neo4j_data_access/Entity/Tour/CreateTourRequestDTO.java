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
    private String name;
    private String description;
    private double start_lat;
    private double start_lng;
    private double end_lat;
    private double end_lng;
    private List<ViennaHistoryWikiBuildingObject> stops;
    private String userId;
}
