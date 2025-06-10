package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {

    @Getter
    @Setter
    @JsonProperty(required = true, value = "quiz")
    private int quiz;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "question_results")
    private List<QuestionResultsDTO> questionResults;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "friend_results")
    private List<QuizFriendResultDTO> friendResults;
}
