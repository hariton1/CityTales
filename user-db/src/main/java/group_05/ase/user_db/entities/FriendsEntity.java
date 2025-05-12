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
    @SequenceGenerator(name = "friendsIdSeq", sequenceName = "seq_friends_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friendsIdSeq")
    private int friendsId;

    private UUID friendOne;

    private UUID friendTwo;

    private LocalDateTime creDat;

}
