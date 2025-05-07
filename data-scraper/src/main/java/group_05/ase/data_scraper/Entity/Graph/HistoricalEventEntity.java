package group_05.ase.data_scraper.Entity.Graph;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Date;
import java.util.Set;

@Node("HistoricalEvent")
@Getter
@Setter
public class HistoricalEventEntity {
    @Id
    private String wikiDataId;
    private String shortDescription;
    private Date eventStartingDate;
    private Date eventEndDate;
    private String wikipediaUrl;

    @Relationship(type = "HAS ASSOCIATION TO", direction = Relationship.Direction.OUTGOING)
    private Set<HistoricalEventEntity> associated_historical_events;

    @Relationship(type = "HAS ASSOCIATION TO", direction = Relationship.Direction.OUTGOING)
    private Set<HistoricalPlaceEntity> associated_historical_places;

    @Relationship(type = "HAS ASSOCIATION TO", direction = Relationship.Direction.OUTGOING)
    private Set<HistoricalPersonEntity> associated_historical_personas;
}
