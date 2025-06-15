package group_05.ase.neo4j_data_access.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FunFactCardDTO {
    private final String headline;
    private final String funFact;
    private final String imageUrl;
    private final double score;
    private final String reason;
}
