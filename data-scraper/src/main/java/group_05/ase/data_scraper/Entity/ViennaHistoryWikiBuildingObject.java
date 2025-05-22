package group_05.ase.data_scraper.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Node("WienGeschichteWikiBuildings")
@Getter
@Setter
@ToString
public class ViennaHistoryWikiBuildingObject {

    @Id
    private int viennaHistoryWikiId;
    private String url;
    private String name;

    private Optional<String> buildingType = Optional.empty();
    private Optional<String> dateFrom = Optional.empty();
    private Optional<String> dateTo = Optional.empty();
    private Optional<String> otherName = Optional.empty();
    private Optional<String> previousName = Optional.empty();
    private Optional<String> namedAfter = Optional.empty();
    private Optional<String> entryNumber = Optional.empty();
    private Optional<String> architect = Optional.empty();
    private Optional<String> famousResidents = Optional.empty();
    private Optional<String> gnd = Optional.empty();
    private Optional<String> wikidataId = Optional.empty();
    private Optional<String> seeAlso = Optional.empty();
    private Optional<String> resource = Optional.empty();
    private Optional<Double> latitude = Optional.empty();
    private Optional<Double> longitude = Optional.empty();

    private List<String> links = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    private String content;
}
