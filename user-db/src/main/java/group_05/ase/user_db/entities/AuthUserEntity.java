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
@Table(name = "users", schema = "auth")
public class AuthUserEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(unique = true, nullable = true)
    private String email;

    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @Column(name = "role", nullable = true)
    private String role;
}
