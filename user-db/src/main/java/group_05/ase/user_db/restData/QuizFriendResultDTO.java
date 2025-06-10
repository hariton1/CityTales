package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizFriendResultDTO {

    @Getter
    @Setter
    @JsonProperty(required = true, value = "quiz")
    private int quiz;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "friend")
    private UUID friend;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "correctness_percentage")
    private Float correctnessPercentage;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "questions_answered")
    private int questionsAnswered;
}
