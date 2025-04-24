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
@Table(name = "user_interest", schema = "public")
@IdClass(UserInterestPK.class)
public class UserInterestEntity {

    @Id
    private String userId;

    @Id
    private int interestId;

    private LocalDateTime creDat;

}
