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
@Table(name = "user_history", schema = "public")
public class UserHistoryEntity {

    @Id
    private int userHistoryId;

    private String userId;

    private int articleId;

    private LocalDateTime openDt;

    private LocalDateTime closeDt;

}
