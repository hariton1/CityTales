package group_05.ase.user_db.entities;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "prices", schema = "public")
public class PriceEntity {

    @Id
    @SequenceGenerator(name = "priceIdSeq", sequenceName = "seq_price_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priceIdSeq")
    private int priceId;

    private int locationId;

    private float price;

    private String name;

    private String description;

    private LocalDateTime created_at;
}
