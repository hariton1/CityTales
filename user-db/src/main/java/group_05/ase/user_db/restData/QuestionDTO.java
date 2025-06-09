package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    @Getter
    @JsonProperty(required = true, value = "id")
    private int id;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "quiz_id")
    private int quiz;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "question")
    private String question;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "answer")
    private String answer;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "wrong_answer_a")
    private String wrongAnswerA;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "wrong_answer_b")
    private String wrongAnswerB;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "wrong_answer_c")
    private String wrongAnswerC;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "image")
    private String image;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "created_at")
    private LocalDateTime createdAt;
}
