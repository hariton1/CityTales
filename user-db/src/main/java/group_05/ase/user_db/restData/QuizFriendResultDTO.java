package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizFriendResultDTO {

    @JsonProperty(required = true, value = "quiz")
    private int quiz;

    @JsonProperty(required = true, value = "friend")
    private UUID friend;

    @JsonProperty(required = true, value = "correctness_percentage")
    private Float correctnessPercentage;

    @JsonProperty(required = true, value = "questions_answered")
    private int questionsAnswered;
}
