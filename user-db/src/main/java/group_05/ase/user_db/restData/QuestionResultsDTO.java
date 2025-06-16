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
public class QuestionResultsDTO {

    @JsonProperty(required = true, value = "id")
    private int id;

    @JsonProperty(required = true, value = "question")
    private int question;

    @JsonProperty(required = true, value = "player")
    private UUID player;

    @JsonProperty(required = true, value = "correct")
    private Boolean correct;

    @JsonProperty(required = true, value = "created_at")
    private LocalDateTime createdAt;

}
