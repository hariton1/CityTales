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
public class UserHistoryDTO {

    @Getter
    @JsonProperty(required = true, value = "user_history_id")
    private int userHistoryId;

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
    @JsonProperty(required = true, value = "open_dt")
    @JsonSerialize(using = CustomTimestampSerializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private LocalDateTime openDt;

    @Getter
    @Setter
    @JsonProperty(value = "close_dt")
    @JsonSerialize(using = CustomTimestampSerializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private LocalDateTime closeDt;

    @Getter
    @Setter
    @JsonProperty(value = "interest_id")
    private int interestId;

}
