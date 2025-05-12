package group_05.ase.neo4j_data_access.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node("HistoricalPerson")
@Getter
@Setter
@ToString
public class HistoricalPersonEntity {
    @Id
    private String wikiDataId;
    private String shortDescription;
    private int yearOfBirth;
    private int yearOfDeath;
    private String name;
    private String wikipediaUrl;
    private String imageUrl;

    @Relationship(type = "HAS ASSOCIATION TO", direction = Relationship.Direction.OUTGOING)
    private Set<HistoricalPlaceEntity> associated_historical_places;

    @Relationship(type = "HAS ASSOCIATION TO", direction = Relationship.Direction.OUTGOING)
    private Set<HistoricalPersonEntity> associated_historical_personas;
}
