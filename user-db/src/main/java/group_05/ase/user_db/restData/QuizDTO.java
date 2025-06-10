package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {

    @Getter
    @Setter
    @JsonProperty(required = true, value = "id")
    private Integer id;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "name")
    private String name;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "description")
    private String description;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "category")
    private String category;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "creator")
    private UUID creator;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "questions")
    private List<QuestionDTO> questions;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "created_at")
    private LocalDateTime createdAt;
}