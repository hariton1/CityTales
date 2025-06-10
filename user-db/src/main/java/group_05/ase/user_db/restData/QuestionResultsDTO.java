package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultsDTO {

    @Getter
    @Setter
    @JsonProperty(required = true, value = "id")
    private int id;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "question")
    private int question;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "player")
    private UUID player;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "correct")
    private Boolean correct;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "created_at")
    private LocalDateTime createdAt;

}
