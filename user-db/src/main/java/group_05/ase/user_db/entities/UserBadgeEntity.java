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
@Table(name = "user_badge", schema = "public")
public class UserBadgeEntity {

    @Id
    @SequenceGenerator(name = "userBadgeIdSeq", sequenceName = "seq_user_badge_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userBadgeIdSeq")
    private int userBadgeId;

    private UUID userId;

    private int articleId;

    private LocalDateTime earnedAt;

}
