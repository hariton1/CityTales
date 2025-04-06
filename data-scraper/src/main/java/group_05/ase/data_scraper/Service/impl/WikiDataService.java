package group_05.ase.data_scraper.Service.impl;

import group_05.ase.data_scraper.Entity.WikiDataObject;
import group_05.ase.data_scraper.Service.IWikiDataService;
import io.github.fastily.jwiki.core.Wiki;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;

@Service
public class WikiDataService implements IWikiDataService {
    private WikibaseDataFetcher wbdf;
    private final String wikiDe = "";
    private final String wikiEn = "enwiki";

    public WikiDataService( ) {
        this.wbdf = new WikibaseDataFetcher(
                BasicApiConnection.getWikidataApiConnection(),
                Datamodel.SITE_WIKIDATA);
    }

    public WikiDataObject extractWikiDataObject(String name) {
        WikiDataObject dataObject = new WikiDataObject();
        dataObject.setWikiName(name);

        EntityDocument ed = null;
        try {
            ed = wbdf.getEntityDocumentByTitle(wikiEn, name);
        } catch (MediaWikiApiErrorException | IOException e) {
            throw new RuntimeException();
        }
        if (ed == null) {
            return dataObject;
        }

        dataObject.setWikiDataId(ed.getEntityId().getId());

        if (ed instanceof ItemDocument) {
            ItemDocument id = ((ItemDocument) ed);
            dataObject.setShortDescription(id.findDescription("en"));
            Statement coord_Statement = id.findStatement("P625");
            if(coord_Statement != null) {
                dataObject.setLocation(coord_Statement.getValue().toString());
            }
        }

        return dataObject;
    }
}
