package group_05.ase.neo4j_data_access.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FunFactResult {
    private final String sentence;
    private final double score;
    private final String reason;
}
