package group_05.ase.data_scraper.Service.Implementation;

import group_05.ase.data_scraper.Entity.WikiDataObject;
import group_05.ase.data_scraper.Service.Interface.IWikiDataService;
import group_05.ase.data_scraper.Service.WikiDataConsts.WikiDataConsts;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;

@Service
public class WikiDataService implements IWikiDataService {

    private static final String DESCRIPTION_LANGUAGE = "en";
    private final WikibaseDataFetcher dataFetcher;

    public WikiDataService() {
        this.dataFetcher = new WikibaseDataFetcher(
                BasicApiConnection.getWikidataApiConnection(),
                Datamodel.SITE_WIKIDATA
        );
    }

    public WikiDataObject extractWikiDataObjectFromEntityId(String entityId) {
        WikiDataObject dataObject = new WikiDataObject();

        EntityDocument entityDocument = fetchEntityDocument(entityId);
        if (entityDocument == null) {
            return dataObject;
        }

        dataObject.setWikiDataId(entityDocument.getEntityId().getId());

        if (entityDocument instanceof ItemDocument itemDocument) {
            populateName(dataObject,itemDocument);
            populateShortDescription(dataObject, itemDocument);
            populateCoordinates(dataObject, itemDocument);
            populateInstanceOf(dataObject, itemDocument);
        }
        return dataObject;
    }

    private EntityDocument fetchEntityDocument(String title) {
        try {
            return dataFetcher.getEntityDocument( title);
        } catch (MediaWikiApiErrorException | IOException e) {
            System.err.println("Failed to fetch entity document for title: " + title);
            return null;
        }
    }

    private void populateName(WikiDataObject dataObject, ItemDocument itemDocument) {
        try{
            dataObject.setWikiName(itemDocument.getLabels().get("en").getText());
        } catch (NullPointerException e) {
            System.out.println("Populate Name of object " + itemDocument.getEntityId().toString() + " went wrong.");
            dataObject.setWikiName("N/A");
        }
    }


    private void populateShortDescription(WikiDataObject dataObject, ItemDocument itemDocument) {
        String description = itemDocument.findDescription(DESCRIPTION_LANGUAGE);
        dataObject.setShortDescription(description);
    }

    private void populateCoordinates(WikiDataObject dataObject, ItemDocument itemDocument) {
        Statement coordinateStatement = itemDocument.findStatement(WikiDataConsts.COORDINATE_PROPERTY_ID);
        if (coordinateStatement != null && coordinateStatement.getValue() != null) {
            dataObject.setLocation(coordinateStatement.getValue().toString());
        }
    }

    private void populateInstanceOf(WikiDataObject dataObject, ItemDocument itemDocument) {
        StatementGroup instanceOfGroup = itemDocument.findStatementGroup(WikiDataConsts.INSTANCE_OF_PROPERTY_ID);
        if (instanceOfGroup == null) {
            return;
        }

        for (Statement statement : instanceOfGroup.getStatements()) {
            if (statement.getValue() != null) {
                String instanceId = extractIdFromUrl(statement.getValue().toString());
                dataObject.addToInstanceOf(instanceId);
            }
        }
    }

    private String extractIdFromUrl(String url) {
        String cleanedUrl = url.split("\\s")[0];
        return cleanedUrl.substring(cleanedUrl.lastIndexOf('/') + 1);
    }
}
