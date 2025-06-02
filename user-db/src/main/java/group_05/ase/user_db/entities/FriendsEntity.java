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
@Table(name = "friends", schema = "public")
public class FriendsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int friendsId;

    private UUID friendOne;
    private UUID friendTwo;
    private LocalDateTime creDat;

    @PrePersist
    public void prePersist() {
        if (creDat == null) {
            creDat = LocalDateTime.now();
        }
    }
}
