package group_05.ase.data_scraper.Service.Implementation;

import group_05.ase.data_scraper.Entity.WikiDataObject;
import group_05.ase.data_scraper.Service.Interface.IWikiDataService;
import group_05.ase.data_scraper.Service.WikiDataConsts.WikiDataConsts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

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
            populateWikipediaUrl(dataObject,itemDocument);
            populateImage(dataObject,itemDocument);
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

    private void populateImage(WikiDataObject dataObject, ItemDocument itemDocument){
        Statement imageStatement = itemDocument.findStatement(WikiDataConsts.IMAGE_PROPERTY_ID);
        if(imageStatement != null){
            String imageName = StringUtils.substringBetween(imageStatement.getMainSnak().toString(), "\"");
            System.out.println(imageName);
            imageName = imageName.replace(" ", "_");
            System.out.println("Image name: " + imageName);
            try{
                URL imageUrl = new URL(String.format("https://commons.wikimedia.org/w/index.php?title=Special:Redirect/file/%s", imageName));
                dataObject.setImageUrl(imageUrl.toString());

            } catch (IOException e) {
                System.out.println("Image to item document URL malformed: " + imageName);
                dataObject.setImageUrl(null);
            }
        } else {
            System.out.println("No image statement found for entity " + itemDocument.getEntityId().toString() + " in Wikidata.");
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

    private void populateWikipediaUrl(WikiDataObject dataObject, ItemDocument itemDocument) {
        if (itemDocument == null || itemDocument.getSiteLinks() == null) {
            return;
        }

        Map<String, SiteLink> siteLinks = itemDocument.getSiteLinks();
        SiteLink siteLink = siteLinks.get("enwiki");

        if (siteLink != null && siteLink.getSiteKey() != null && siteLink.getPageTitle() != null) {
            String baseUrl = "https://" + siteLink.getSiteKey().replace("wiki", "") + ".wikipedia.org/wiki/";
            String fullUrl = baseUrl + siteLink.getPageTitle().replace(" ", "_");

            dataObject.setWikipediaUrl(fullUrl);
        } else {
            System.out.println("No valid English Wikipedia link found.");
        }
    }

    private String extractIdFromUrl(String url) {
        String cleanedUrl = url.split("\\s")[0];
        return cleanedUrl.substring(cleanedUrl.lastIndexOf('/') + 1);
    }
}
