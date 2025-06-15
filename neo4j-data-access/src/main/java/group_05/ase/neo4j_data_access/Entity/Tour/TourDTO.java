package group_05.ase.neo4j_data_access.Entity.Tour;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TourDTO {
    Integer id;
    String name;
    String description;
    Double start_lat;
    Double start_lng;
    Double end_lat;
    Double end_lng;
    String stops;
    Double distance;
    Double durationEstimate;
    String userId;
    Double tourPrice;
    String pricePerStop;
}
