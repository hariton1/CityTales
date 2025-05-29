package group_05.ase.user_db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "article_weight", schema = "public")
public class ArticleWeightEntity {

    @Id
    @SequenceGenerator(name = "articleWeightIdSeq", sequenceName = "seq_article_weight_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articleWeightIdSeq")
    private int articleWeightId;

    private int articleId;

    private float weight;

}
