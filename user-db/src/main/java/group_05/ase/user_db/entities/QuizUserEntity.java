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
@Table(name = "quiz_user", schema = "public")
public class QuizUserEntity {

    @Id
    @SequenceGenerator(name = "quizUserIdSeq", sequenceName = "seq_quiz_user_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quizUserIdSeq")
    private int id;

    private int quiz;

    private UUID player;

    private LocalDateTime createdAt;
}
