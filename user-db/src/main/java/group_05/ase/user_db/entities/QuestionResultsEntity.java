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
@Table(name = "question_results", schema = "public")
public class QuestionResultsEntity {

    @Id
    @SequenceGenerator(name = "questionIdSeq", sequenceName = "seq_question_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questionIdSeq")
    private int id;

    private int question;

    private UUID player;

    private Boolean correct;

    private LocalDateTime createdAt;
}
