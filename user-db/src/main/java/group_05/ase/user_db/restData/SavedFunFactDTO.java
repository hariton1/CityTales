package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import group_05.ase.user_db.formaters.CustomTimestampDeserializer;
import group_05.ase.user_db.formaters.CustomTimestampSerializer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedFunFactDTO {

    @Getter
    @JsonProperty(required = true, value = "saved_fun_fact_id")
    private int savedFunFactId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "user_id")
    private UUID userId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "article_id")
    private int articleId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "headline")
    private String headline;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "fun_fact")
    private String funFact;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "image_url")
    private String imageUrl;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "score")
    private float score;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "reason")
    private String reason;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "saved_at")
    @JsonSerialize(using = CustomTimestampSerializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private LocalDateTime savedAt;

    @Override
    public String toString() {
        return "SavedFunFactDTO{" +
                "savedFunFactId=" + savedFunFactId +
                ", userId=" + userId.toString() +
                ", articleId=" + articleId +
                ", headline='" + headline + '\'' +
                ", funFact='" + funFact + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", score=" + score +
                ", reason='" + reason + '\'' +
                ", savedAt=" + savedAt +
                '}';
    }

}
