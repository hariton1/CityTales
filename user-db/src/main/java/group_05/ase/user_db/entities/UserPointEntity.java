package group_05.ase.user_db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_point", schema = "public")
public class UserPointEntity {

    @Id
    @SequenceGenerator(name = "userPointIdSeq", sequenceName = "seq_user_point_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userPointIdSeq")
    private int userPointId;

    private UUID userId;

    private int points;

    private LocalDateTime earnedAt;

    private int articleId;

}
