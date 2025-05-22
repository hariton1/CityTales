package group_05.ase.data_scraper.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Node("WienGeschichteWikiPersons")
@Getter
@Setter
@ToString
public class ViennaHistoryWikiPersonObject {

    @Id
    private int viennaHistoryWikiId;
    private String url;
    private String name;

    private Optional<String> personName = Optional.empty();
    private Optional<String> alternativeName = Optional.empty();
    private Optional<String> titles = Optional.empty();
    private Optional<String> sex = Optional.empty();
    private Optional<String> gnd = Optional.empty();
    private Optional<String> wikidataId = Optional.empty();
    private Optional<String> birthDate = Optional.empty();
    private Optional<String> birthPlace = Optional.empty();
    private Optional<String> deathDate = Optional.empty();
    private Optional<String> deathPlace = Optional.empty();
    private Optional<String> jobs = Optional.empty();
    private Optional<String> politicalLinkage = Optional.empty();
    private Optional<String> event = Optional.empty();
    private Optional<String> estate = Optional.empty();
    private Optional<String> seeAlso = Optional.empty();
    private Optional<String> resource = Optional.empty();

    private List<String> links = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    private String content;
}
