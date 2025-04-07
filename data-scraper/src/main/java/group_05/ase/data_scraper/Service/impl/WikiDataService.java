package group_05.ase.data_scraper.Service.impl;

import group_05.ase.data_scraper.Entity.WikiDataObject;
import group_05.ase.data_scraper.Service.IWikiDataService;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WbGetEntitiesSearchData;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WikiDataService implements IWikiDataService {

    private static final String ENGLISH_WIKIPEDIA = "enwiki";
    private static final String DESCRIPTION_LANGUAGE = "en";
    private static final String COORDINATE_PROPERTY_ID = "P625";
    private static final String INSTANCE_OF_PROPERTY_ID = "P31";

    private final WikibaseDataFetcher dataFetcher;

    public WikiDataService() {
        this.dataFetcher = new WikibaseDataFetcher(
                BasicApiConnection.getWikidataApiConnection(),
                Datamodel.SITE_WIKIDATA
        );
    }

    @Override
    public WikiDataObject extractWikiDataObject(String name) {
        WikiDataObject dataObject = new WikiDataObject();
        dataObject.setWikiName(name);

        EntityDocument entityDocument = fetchEntityDocument(name);
        if (entityDocument == null) {
            return dataObject;
        }

        dataObject.setWikiDataId(entityDocument.getEntityId().getId());

        if (entityDocument instanceof ItemDocument itemDocument) {
            populateShortDescription(dataObject, itemDocument);
            populateCoordinates(dataObject, itemDocument);
            populateInstanceOf(dataObject, itemDocument);
        }

        return dataObject;
    }

    public List<WikiDataObject> findPlaces() {
        List<WikiDataObject> list = new ArrayList<>();
        try {
            List<WbSearchEntitiesResult> result = dataFetcher.searchEntities("");
            WbGetEntitiesSearchData wbGetEntitiesSearchData = new WbGetEntitiesSearchData();
            System.out.println("List size: " + result.size());
            for (WbSearchEntitiesResult res:result) {
                System.out.println(res.getUrl());
                WikiDataObject dataObject = new WikiDataObject();

                String label = res.getLabel();
                String entityId = res.getEntityId();
                System.out.println("Checking: " + label + "; Id: " + entityId);
                /*

                EntityDocument entityDocument = fetchEntityDocument(label);
                if (entityDocument == null) {
                    break;
                }

                if (entityDocument instanceof ItemDocument itemDocument) {
                    if (!isPlace(itemDocument)) {
                        break;
                    }
                    dataObject.setWikiDataId(entityId);
                    dataObject.setWikiName(label);
                    populateShortDescription(dataObject, itemDocument);
                    populateCoordinates(dataObject, itemDocument);
                    populateInstanceOf(dataObject, itemDocument);
                    list.add(dataObject);
                }*/
            }
        } catch (MediaWikiApiErrorException | IOException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean isPlace(ItemDocument itemDocument) {
        Statement coordinateStatement = itemDocument.findStatement(COORDINATE_PROPERTY_ID);
        return coordinateStatement != null && coordinateStatement.getValue() != null;
    }

    private EntityDocument fetchEntityDocument(String title) {
        try {
            return dataFetcher.getEntityDocumentByTitle(ENGLISH_WIKIPEDIA, title);
        } catch (MediaWikiApiErrorException | IOException e) {
            throw new RuntimeException("Failed to fetch entity document for title: " + title, e);
        }
    }

    private void populateShortDescription(WikiDataObject dataObject, ItemDocument itemDocument) {
        String description = itemDocument.findDescription(DESCRIPTION_LANGUAGE);
        dataObject.setShortDescription(description);
    }

    private void populateCoordinates(WikiDataObject dataObject, ItemDocument itemDocument) {
        Statement coordinateStatement = itemDocument.findStatement(COORDINATE_PROPERTY_ID);
        if (coordinateStatement != null && coordinateStatement.getValue() != null) {
            dataObject.setLocation(coordinateStatement.getValue().toString());
        }
    }

    private void populateInstanceOf(WikiDataObject dataObject, ItemDocument itemDocument) {
        StatementGroup instanceOfGroup = itemDocument.findStatementGroup(INSTANCE_OF_PROPERTY_ID);
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
