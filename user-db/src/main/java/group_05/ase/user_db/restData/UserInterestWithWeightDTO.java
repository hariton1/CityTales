package group_05.ase.user_db.restData;

import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInterestWithWeightDTO {
    private int interestId;
    private String interestNameEn;
    private String description;
    private String interestNameDe;
    private float interestWeight;            // <-- comes from UserInterestEntity
    private LocalDateTime creDat;            // <-- if you want creation date too
}
