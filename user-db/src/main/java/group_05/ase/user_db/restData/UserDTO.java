package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @Getter
    @JsonProperty(required = true, value = "id")
    private UUID id;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "email")
    private String email;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "created_at")
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "role")
    private String role;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "status")
    private String status;
}
