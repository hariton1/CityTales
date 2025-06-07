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
@Table(name = "user_data", schema = "public")
public class UserDataEntity {

    @Id
    @SequenceGenerator(name = "userDataIdSeq", sequenceName = "seq_user_data_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userDataIdSeq")
    private int userDataId;

    private UUID userId;

    private String roleName;

    private String status;

    private LocalDateTime creDat;

}
