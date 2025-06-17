package group_05.ase.user_db.restData;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuizQuestionResponse {

    private String question;
    private String answer;
    private String wrongAnswerA;
    private String wrongAnswerB;
    private String wrongAnswerC;
    private String image;
}
