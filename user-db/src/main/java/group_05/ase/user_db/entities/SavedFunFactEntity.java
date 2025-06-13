package group_05.ase.user_db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "saved_fun_fact", schema = "public")
public class SavedFunFactEntity {

    @Id
    @SequenceGenerator(name = "savedFunFactIdSeq", sequenceName = "seq_saved_fun_fact_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "savedFunFactIdSeq")
    private int savedFunFactId;

    private UUID userId;

    private int articleId;

    private String headline;

    private String funFact;

    private String imageUrl;

    private float score;

    private String reason;

    private LocalDateTime savedAt;

}
