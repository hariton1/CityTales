
package group_05.ase.auth;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users", schema = "auth")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role = "USER";
}
