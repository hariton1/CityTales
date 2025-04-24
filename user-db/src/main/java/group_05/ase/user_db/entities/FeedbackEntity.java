package group_05.ase.user_db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "feedback", schema = "public")
public class FeedbackEntity {

    @Id
    private int feedbackId;

    private String userId;

    private int articleId;

    private double rating;

    private String fbContent;

    private LocalDateTime creDat;

}
