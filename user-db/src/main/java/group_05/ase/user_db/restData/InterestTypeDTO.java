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
public class InterestTypeDTO {

    @Getter
    @JsonProperty(required = true, value = "interest_type_id")
    private int interestTypeId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "type_name")
    private String typeName;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "description")
    private String description;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "cre_dat")
    @JsonSerialize(using = CustomTimestampSerializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private LocalDateTime creDat;

}
