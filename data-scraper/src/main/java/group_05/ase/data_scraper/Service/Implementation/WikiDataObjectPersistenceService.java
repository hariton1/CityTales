package group_05.ase.data_scraper.Service.Implementation;

import group_05.ase.data_scraper.Entity.Graph.HistoricalPersonEntity;
import group_05.ase.data_scraper.Entity.Graph.HistoricalPlaceEntity;
import group_05.ase.data_scraper.Entity.WikiDataObject;
import group_05.ase.data_scraper.Service.Interface.IWikiDataObjectPersistenceService;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.springframework.data.neo4j.types.GeographicPoint2d;
import org.springframework.stereotype.Service;

import static org.neo4j.driver.Values.parameters;

@Service
public class WikiDataObjectPersistenceService implements IWikiDataObjectPersistenceService {

    private final String NEO4JURL = "bolt://localhost:7687";
    private final String NEO4JUSER = "neo4j";
    private final String NEO4JPW = "password";

    private Driver driver;

    public WikiDataObjectPersistenceService(){
        AuthToken authToken = AuthTokens.basic(NEO4JUSER, NEO4JPW);
        try {
            driver = GraphDatabase.driver(NEO4JURL, authToken);
        } catch (Exception e) {
            System.out.println("Could not initialize db driver");
        }
    }

    @Override
    public void persistHistoricPerson(WikiDataObject wikiDataObject) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    Result result = tx.run("MERGE (a:HistoricPerson {wikiDataId: $wikiDataId, name: $name, shortDescription: coalesce($shortDescription, \"N/A\")}) RETURN a.name",
                            parameters("name", wikiDataObject.getWikiName(),
                                    "wikiDataId", wikiDataObject.getWikiDataId(),
                                    "shortDescription", wikiDataObject.getShortDescription()));
                    return result.single().get(0).asString();
                }
            });
            System.out.println("Created Historic Person: " + message);
        }
    }

    @Override
    public void persistHistoricPlace(WikiDataObject wikiDataObject) {
        try (Session session = driver.session()) {
            String message = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    Result result = tx.run("MERGE (a:HistoricPlace {wikiDataId: $wikiDataId, name: $name, shortDescription: coalesce($shortDescription, \"N/A\") , location: $location}) RETURN a.name",
                            parameters("name", wikiDataObject.getWikiName(),
                                    "wikiDataId", wikiDataObject.getWikiDataId(),
                                    "shortDescription", wikiDataObject.getShortDescription(),
                                    "location", wikiDataObject.getLocation()));
                    return result.single().get(0).asString();
                }
            });
            System.out.println("Created Historic Place: " + message);
        }
    }

    @Override
    public void persistHistoricEvent(WikiDataObject wikiDataObject) {
    }

    @Override
    public HistoricalPersonEntity getPersonByName(String name) {

        try (Session session = driver.session()){
            String query = "MATCH (p:HistoricPerson {name: $name}) RETURN p";
            Record record = session.executeRead(tx ->
                tx.run(query, Values.parameters("name", name)).single());

            Node node = record.get("p").asNode();
            HistoricalPersonEntity personEntity = new HistoricalPersonEntity();
            personEntity.setWikiDataId(node.get("wikiDataId").asString());
            personEntity.setShortDescription(node.get("shortDescription").asString());
            personEntity.setYearOfBirth(0);
            personEntity.setYearOfDeath(0);
            personEntity.setName(node.get("name").asString());

            return personEntity;

        } catch (NoSuchRecordException e) {
            System.err.println("The requested entity does not exist!");
            return null;
        }
    }

    @Override
    public HistoricalPlaceEntity getPlaceByName(String name) {
        return null;
    }

    @Override
    public HistoricalPlaceEntity getPlaceByCoordinates(GeographicPoint2d coordinates) {
        return null;
    }


}
