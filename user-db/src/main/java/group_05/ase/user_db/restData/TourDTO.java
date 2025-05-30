package group_05.ase.user_db.restData;

import lombok.*;

import java.util.List;

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
}
