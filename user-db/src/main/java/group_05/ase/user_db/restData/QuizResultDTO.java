package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {

    @JsonProperty(required = true, value = "quiz")
    private int quiz;

    @JsonProperty(required = true, value = "questionResults")
    private List<QuestionResultsDTO> questionResults;

    @JsonProperty(required = true, value = "friendResults")
    private List<QuizFriendResultDTO> friendResults;
}
