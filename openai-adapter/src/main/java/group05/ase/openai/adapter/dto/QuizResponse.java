package group05.ase.openai.adapter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    private String question;
    private String answer;
    private String wrongAnswerA;
    private String wrongAnswerB;
    private String wrongAnswerC;
    private String image;
}
