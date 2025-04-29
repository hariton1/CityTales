package group_05.ase.user_db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
    private int interestTypeId;

    private String typeName;

    private String description;

    private LocalDateTime creDat;

}
