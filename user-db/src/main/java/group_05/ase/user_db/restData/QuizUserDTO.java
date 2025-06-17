package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizUserDTO {

    @JsonProperty(required = true, value = "id")
    private int id;

    @JsonProperty(required = true, value = "quiz")
    private int quiz;

    @JsonProperty(required = true, value = "player")
    private UUID player;

    @JsonProperty(required = true, value = "createdAt")
    private LocalDateTime createdAt;
}
