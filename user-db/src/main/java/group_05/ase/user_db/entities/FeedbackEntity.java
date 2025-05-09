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
@Table(name = "feedback", schema = "public")
public class FeedbackEntity {

    @Id
    @SequenceGenerator(name = "feedbackIdSeq", sequenceName = "seq_feedback_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedbackIdSeq")
    private int feedbackId;

    private UUID userId;

    private UUID articleId;

    private double rating;

    private String fbContent;

    private LocalDateTime creDat;

}
