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
@Table(name = "user_history", schema = "public")
public class UserHistoryEntity {

    @Id
    @SequenceGenerator(name = "userHistoryIdSeq", sequenceName = "seq_user_history_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userHistoryIdSeq")
    private int userHistoryId;

    private UUID userId;

    private UUID articleId;

    private LocalDateTime openDt;

    private LocalDateTime closeDt;

    private int interestId;

}
