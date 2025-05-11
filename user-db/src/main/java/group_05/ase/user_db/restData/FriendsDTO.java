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
public class FriendsDTO {

    @Getter
    @JsonProperty(required = true, value = "friends_id")
    private int friendsId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "friend_one")
    private UUID friendOne;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "friend_two")
    private UUID friendTwo;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "cre_dat")
    @JsonSerialize(using = CustomTimestampSerializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private LocalDateTime creDat;

}
