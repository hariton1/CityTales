package group_05.ase.user_db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tours", schema = "public")

public class TourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
