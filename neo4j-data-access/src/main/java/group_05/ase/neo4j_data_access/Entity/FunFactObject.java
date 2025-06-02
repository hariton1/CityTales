package group_05.ase.neo4j_data_access.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunFactObject {
    private String title;
    private String funFact;
}
