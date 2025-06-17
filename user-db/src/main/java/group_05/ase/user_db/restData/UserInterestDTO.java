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
public class UserInterestDTO {

    @Getter
    @JsonProperty(required = true, value = "user_id")
    private UUID userId;

    @Getter
    @JsonProperty(required = true, value = "interest_id")
    private int interestId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "cre_dat")
    @JsonSerialize(using = CustomTimestampSerializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private LocalDateTime creDat;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "interest_weight")
    private float interestWeight;

    @Override
    public String toString() {
        return "UserInterestDTO{" +
                "userId=" + userId.toString() +
                ", interestId=" + interestId +
                ", creDat=" + creDat +
                ", interestWeight=" + interestWeight +
                '}';
    }

}
