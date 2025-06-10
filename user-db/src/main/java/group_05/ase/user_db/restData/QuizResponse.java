package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    @Getter
    @Setter
    @JsonProperty(required = true, value = "answer")
    private String answer;
}
