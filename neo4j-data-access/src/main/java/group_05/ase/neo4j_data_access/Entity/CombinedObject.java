package group_05.ase.neo4j_data_access.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class CombinedObject {

    @Id
    private int viennaHistoryWikiId;
    private String url;
    private String name;
    private List<String> imageUrls = new ArrayList<>();
}
