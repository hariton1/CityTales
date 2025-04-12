package group_05.ase.data_scraper.Service.Implementation;

import group_05.ase.data_scraper.Entity.WikiDataObject;
import group_05.ase.data_scraper.Service.Interface.IWikiDataService;
import group_05.ase.data_scraper.Service.WikiDataConsts.WikiDataConsts;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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



    public WikiDataObject extractWikiDataObjectFromEntityName(String name) {
        WikiDataObject dataObject = new WikiDataObject();

        EntityDocument entityDocument = fetchEntityDocument(name);
        if (entityDocument == null) {
            return dataObject;
        }

        dataObject.setWikiDataId(entityDocument.getEntityId().getId());
        dataObject.setWikiName(name);

        if (entityDocument instanceof ItemDocument itemDocument) {
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
        dataObject.setWikiName(itemDocument.getLabels().get("en").getText());
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

    public void extractWikiDataObjectList(List<String> names, List<WikiDataObject> people,List<WikiDataObject> places) {

        int progress_counter = 0;
        for (String pageName:names) {
            System.out.println("checking: " + pageName);
            WikiDataObject wikiDO = extractWikiDataObjectFromEntityName(pageName);

            boolean isPerson = wikiDO.getInstanceOf().stream().anyMatch(WikiDataConsts.PERSON_CODES::contains);
            boolean isPlace = wikiDO.getInstanceOf().stream().anyMatch(WikiDataConsts.PLACE_CODES::contains);

            if (isPerson) {
                people.add(wikiDO);
                // System.out.println("Person found!: " + wikiDO);
                appendToFile("people.txt", wikiDO.toString());
            } else if (isPlace) {
                places.add(wikiDO);
                // System.out.println("Place found!: " + wikiDO);
                appendToFile("places.txt", wikiDO.toString());
            }
            //System.out.println("Status: " + progress_counter);
            //progress_counter++;
        }
    }

    private void appendToFile(String filePath, String content) {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileWriter fw = new FileWriter(file, true)) {
                fw.write(content + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error writing to " + filePath + ": " + e.getMessage());
        }
    }
}
