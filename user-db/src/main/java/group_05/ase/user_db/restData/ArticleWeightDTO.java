package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleWeightDTO {

    @Getter
    @JsonProperty(required = true, value = "article_weight_id")
    private int articleWeightId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "article_id")
    private int articleId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "weight")
    private float weight;

}
