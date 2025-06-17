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
public class UserDataDTO {

    @Getter
    @JsonProperty(required = true, value = "user_data_id")
    private int userDataId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "user_id")
    private UUID userId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "role_name")
    private String roleName;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "status")
    private String status;

    @Getter
    @Setter
    @JsonProperty(value = "cre_dat")
    @JsonSerialize(using = CustomTimestampSerializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private LocalDateTime creDat;

    @Override
    public String toString() {
        return "UserDataDTO{" +
                "userDataId=" + userDataId +
                ", userId=" + userId.toString() +
                ", roleName='" + roleName + '\'' +
                ", status='" + status + '\'' +
                ", creDat=" + creDat +
                '}';
    }

}
