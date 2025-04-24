package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import group_05.ase.user_db.formaters.CustomTimestampDeserializer;
import group_05.ase.user_db.formaters.CustomTimestampSerializer;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {

    @Getter
    @JsonProperty(required = true, value = "feedback_id")
    private int feedbackId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "user_id")
    private String userId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "article_id")
    private int articleId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "rating")
    private double rating;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "fb_content")
    private String fbContent;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "cre_dat")
    @JsonSerialize(using = CustomTimestampSerializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private LocalDateTime creDat;

}
