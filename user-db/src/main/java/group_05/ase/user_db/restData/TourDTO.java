package group_05.ase.user_db.restData;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TourDTO {

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
}
