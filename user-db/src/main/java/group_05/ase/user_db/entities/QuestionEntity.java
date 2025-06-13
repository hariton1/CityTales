package group_05.ase.user_db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "question", schema = "public")
public class QuestionEntity {

    @Id
    @SequenceGenerator(name = "questionIdSeq", sequenceName = "seq_question_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questionIdSeq")
    private int id;

    private int quiz;

    private String question;

    private String answer;

    private String wrong_answer_a;

    private String wrong_answer_b;

    private String wrong_answer_c;

    private String image;

    private LocalDateTime created_at;
}
