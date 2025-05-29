package group_05.ase.neo4j_data_access.Entity.Tour;


import lombok.*;
import org.springframework.data.neo4j.types.GeographicPoint2d;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TourObject {

    private String name;
    private GeographicPoint2d start;
    private GeographicPoint2d end;
    private List<TourStop> stops;
    private double distance;
    private double durationEstimate;

}
