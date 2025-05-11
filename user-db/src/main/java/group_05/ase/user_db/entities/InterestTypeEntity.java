package group_05.ase.user_db.entities;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interest_type", schema = "public")
public class InterestTypeEntity {

    @Id
    @SequenceGenerator(name = "interestTypeSeq", sequenceName = "seq_interest_type_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interestTypeSeq")
    private int interestTypeId;

    private String typeName;

    private String description;

    private LocalDateTime creDat;

}
