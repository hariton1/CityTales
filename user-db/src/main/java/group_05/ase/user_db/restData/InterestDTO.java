package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestDTO {

    @Getter
    @JsonProperty(required = true, value = "interest_id")
    private int interestId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "interest_type_id")
    private int interestTypeId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "interest_name")
    private String interestName;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "description")
    private String description;

}
