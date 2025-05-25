package group_05.ase.neo4j_data_access.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Node("WienGeschichteWikiEvents")
@Getter
@Setter
@ToString
public class ViennaHistoryWikiEventObject {

    @Id
    private int viennaHistoryWikiId;
    private String url;
    private String name;

    private Optional<String> typeOfEvent = Optional.empty();
    private Optional<String> dateFrom = Optional.empty();
    private Optional<String> dateTo = Optional.empty();
    private Optional<String> topic = Optional.empty();
    private Optional<String> organizer = Optional.empty();
    private Optional<String> participantCount = Optional.empty();
    private Optional<Boolean> violence = Optional.empty();
    private Optional<String> gnd = Optional.empty();
    private Optional<String> wikidataId = Optional.empty();
    private Optional<String> seeAlso = Optional.empty();
    private Optional<String> resource = Optional.empty();

    private List<String> links = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    private String contentGerman;
    private String contentEnglish;
}
