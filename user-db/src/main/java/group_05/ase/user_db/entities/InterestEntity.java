package group_05.ase.user_db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interest", schema = "public")
public class InterestEntity {

    @Id
    private int interestId;

    private int interestTypeId;

    private String interestName;

    private String description;

}
