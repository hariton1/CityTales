package group_05.ase.user_db.entities;

import jakarta.persistence.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interest", schema = "public")
public class InterestEntity {

    @Id
    @SequenceGenerator(name = "interestIdSeq", sequenceName = "seq_interest_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interestIdSeq")
    private int interestId;

    private String interestNameEn;

    private String description;

    private String interestNameDe;

}
