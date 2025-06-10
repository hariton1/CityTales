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
@Table(name = "quiz", schema = "public")
public class QuizEntity {

    @Id
    @SequenceGenerator(name = "quizIdSeq", sequenceName = "seq_quiz_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quizIdSeq")
    private Integer id;

    private String name;

    private String description;

    private UUID creator;

    private String category;

    private LocalDateTime createdAt;
}
