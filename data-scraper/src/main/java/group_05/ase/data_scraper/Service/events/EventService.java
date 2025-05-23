package group_05.ase.data_scraper.Service.events;

import group_05.ase.data_scraper.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.data_scraper.Service.embeddings.OpenAiService;
import group_05.ase.data_scraper.Service.general.ManualExtractorService;
import group_05.ase.data_scraper.Service.general.ContentService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class EventService {


    public String eventSeed = "https://www.geschichtewiki.wien.gv.at/Kategorie:Ereignisse";
    private final ManualExtractorService manualExtractorService;
    private final EventRepository eventRepository;
    private final Set<String> allEvents = new HashSet<>();
    private final ContentService contentService;
    private final OpenAiService openAiService;


    public EventService(ManualExtractorService manualExtractorService, EventRepository wikiEventPersistenceService, ContentService contentService, OpenAiService openAiService) {
        this.manualExtractorService = manualExtractorService;
        this.eventRepository = wikiEventPersistenceService;
        this.contentService = contentService;
        this.openAiService = openAiService;
    }

    public void search(int limit) {
        try {
            int[] persistedCount = {0};  // mutable counter
            this.scrapeCategory(eventSeed, limit, persistedCount);
            System.out.println("#Total events: " + allEvents.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void scrapeCategory(String url, int limit, int[] persistedCount) throws IOException {
        if (persistedCount[0] >= limit) return;

        Document doc = Jsoup.connect(url).get();

        // get sub-categories
        Element mwCategoriesDiv = doc.getElementById("mw-subcategories");
        if (mwCategoriesDiv != null) {
            Elements categoryLinks = mwCategoriesDiv.select("a[href^=\"/Kategorie:\"]");
            for (Element link : categoryLinks) {
                if (persistedCount[0] >= limit) return;
                String fullUrl = "https://www.geschichtewiki.wien.gv.at" + link.attr("href");
                scrapeCategory(fullUrl, limit, persistedCount);
            }
        }

        // get actual events
        Element mwPagesDiv = doc.getElementById("mw-pages");
        if (mwPagesDiv != null) {
            Elements links = mwPagesDiv.select("a");

            List<ViennaHistoryWikiEventObject> pageEntries = links.stream()
                    .limit(20) // Optional: cap how many links are processed per page
                    .parallel()
                    .map(link -> extractEventInfos(link.attr("abs:href"), link.text()))
                    .filter(Objects::nonNull)
                    .toList();

            for (ViennaHistoryWikiEventObject obj : pageEntries) {
                if (persistedCount[0] >= limit) return;
                eventRepository.persistViennaHistoryWikiEventObject(obj);
                persistedCount[0]++;
            }

            // pagination
            Elements nextPageLinks = mwPagesDiv.select("a:contains(nächste Seite)");
            if (!nextPageLinks.isEmpty() && persistedCount[0] < limit) {
                String nextPageHref = nextPageLinks.first().attr("href");
                String nextPageUrl = "https://www.geschichtewiki.wien.gv.at" + nextPageHref;
                scrapeCategory(nextPageUrl, limit, persistedCount);
            }
        }
    }

    public ViennaHistoryWikiEventObject extractEventInfos(String url, String text) {
        try {
            Document doc = Jsoup.connect(url).get();

            ViennaHistoryWikiEventObject wikiObject = new ViennaHistoryWikiEventObject();
            wikiObject.setUrl(url);
            wikiObject.setName(text);

            // Extract all links from <p> tags and images from <img> tags & content
            wikiObject.setLinks(manualExtractorService.getLinks(doc));
            wikiObject.setImageUrls(manualExtractorService.getImageUrls(doc));
            wikiObject.setContentGerman(contentService.extractMainArticleText(doc));

            Element table = doc.selectFirst("table.table.table-condensed.table-hover");

            if (table != null) {
                Elements rows = table.select("tr");
                for (Element row : rows) {
                    Elements tds = row.select("td");

                    if (tds.size() >= 2) {
                        String key = tds.get(0).text().trim();
                        String value = tds.get(1).text().trim();

                        // If-stack
                        if (key.equals("Art des Ereignisses")) {
                            wikiObject.setTypeOfEvent(Optional.of(value));
                        } else if (key.equals("Datum vonDatum (oder Jahr) von")) {
                            wikiObject.setDateFrom(Optional.of(value));
                        } else if(key.equals("Datum bisDatum (oder Jahr) bis")) {
                            wikiObject.setDateTo(Optional.of(value));
                        } else if(key.equals("Thema")) {
                            wikiObject.setTopic(Optional.of(value));
                        } else if(key.equals("VeranstalterVeranstalter")) {
                            wikiObject.setOrganizer(Optional.of(value));
                        } else if(key.equals("Teilnehmerzahl")) {
                            wikiObject.setParticipantCount(Optional.of(value));
                        } else if(key.equals("Gewalt")) {
                            if ("Ja".equalsIgnoreCase(value)) {
                                wikiObject.setViolence(Optional.of(true));
                            } else if ("Nein".equalsIgnoreCase(value)) {
                                wikiObject.setViolence(Optional.of(false));
                            } else {
                                wikiObject.setViolence(Optional.empty());
                            }
                        } else if(key.equals("Wien Geschichte WikiIdentifier/Persistenter URL zur Seite ᵖ")) {
                            wikiObject.setViennaHistoryWikiId(Integer.parseInt(value));
                        } else if(key.equals("GNDGemeindsame Normdatei")) {
                            wikiObject.setGnd(Optional.of(value));
                        } else if(key.equals("WikidataIDID von Wikidata")) {
                            wikiObject.setWikidataId(Optional.of(value));
                        } else if(key.equals("Siehe auchVerweist auf andere Objekte im Wiki ᵖ")) {
                            wikiObject.setSeeAlso(Optional.of(value));
                        } else if(key.equals("RessourceUrsprüngliche Ressource ᵖ")) {
                            wikiObject.setResource(Optional.of(value));
                        }
                    }
                }
            }
            // Embeddings
            if (wikiObject.getContentGerman() != null  && !wikiObject.getContentGerman().equals("")) {
                System.out.println("Content!: " + wikiObject.getContentGerman());
                float[] embedding = openAiService.getEmbedding(wikiObject.getContentGerman());
                eventRepository.persistEmbedding(embedding,wikiObject.getViennaHistoryWikiId());
                wikiObject.setContentEnglish("");
            }

            return wikiObject;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
