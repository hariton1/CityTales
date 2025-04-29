package group_05.ase.data_scraper.Service.Interface;

import group_05.ase.data_scraper.Entity.Graph.HistoricalPersonEntity;
import group_05.ase.data_scraper.Entity.Graph.HistoricalPlaceEntity;
import group_05.ase.data_scraper.Entity.WikiDataObject;
import org.springframework.data.neo4j.types.GeographicPoint2d;

public interface IWikiDataObjectPersistenceService {

    void persistHistoricPerson(WikiDataObject wikiDataObject);
    void persistHistoricPlace(WikiDataObject wikiDataObject);
    void persistHistoricEvent(WikiDataObject wikiDataObject);

    HistoricalPersonEntity getPersonByName(String name);
    HistoricalPlaceEntity getPlaceByName(String name);
    HistoricalPlaceEntity getPlaceByCoordinates(GeographicPoint2d coordinates);

}
