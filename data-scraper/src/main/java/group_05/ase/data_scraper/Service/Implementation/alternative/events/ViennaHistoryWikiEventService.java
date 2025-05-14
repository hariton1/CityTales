package group_05.ase.data_scraper.Service.Implementation.alternative.events;

import group_05.ase.data_scraper.Entity.ManualScraping.ViennaHistoryWikiEventObject;
import group_05.ase.data_scraper.Service.Implementation.alternative.general.ManualExtractorService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ViennaHistoryWikiEventService {


    public String eventSeed = "https://www.geschichtewiki.wien.gv.at/Kategorie:Ereignisse";
    private final ManualExtractorService manualExtractorService;
    private final ViennaHistoryWikiEventPersistenceService wikiEventPersistenceService;
    private final Set<String> allEvents = new HashSet<>();

    public ViennaHistoryWikiEventService( ManualExtractorService manualExtractorService, ViennaHistoryWikiEventPersistenceService wikiEventPersistenceService) {
        this.manualExtractorService = manualExtractorService;
        this.wikiEventPersistenceService = wikiEventPersistenceService;
    }

    public void getAllEvents() {
        allEvents.forEach(System.out::println);
        System.out.println("Total Events retrieved: " + allEvents.size());
    }

    public void scrapeCategory(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        // get sub-categories
        Element mwCategoriesDiv = doc.getElementById("mw-subcategories");

        if (mwCategoriesDiv != null) {
            Elements categoryLinks = mwCategoriesDiv.select("a[href^=\"/Kategorie:\"]");

            for (Element link : categoryLinks) {
                String fullUrl = "https://www.geschichtewiki.wien.gv.at" + link.attr("href");
                //System.out.println("Sub-category: " + fullUrl);
                scrapeCategory(fullUrl);
            }
        }

        // get actual events
        Element mwPagesDiv = doc.getElementById("mw-pages");

        if (mwPagesDiv != null) {
            Elements links = mwPagesDiv.select("a");

            for (Element link : links) {
                String fullUrl = "https://www.geschichtewiki.wien.gv.at" + link.attr("href");
                System.out.println("Event: " + fullUrl);
                allEvents.add(fullUrl);
            }

            List<ViennaHistoryWikiEventObject> pageEntries = links.stream()
                    .limit(200)
                    .parallel()
                    .map(link -> {
                        return extractEventInfos(link.attr("abs:href"), link.text());
                    })
                    .filter(obj -> obj != null)
                    .toList();

            for (ViennaHistoryWikiEventObject obj:pageEntries) {
                wikiEventPersistenceService.persistViennaHistoryWikiEventObject(obj);
            }
            // pagination
            Elements nextPageLinks = mwPagesDiv.select("a:contains(nächste Seite)");
            if (!nextPageLinks.isEmpty()) {
                String nextPageHref = nextPageLinks.first().attr("href");
                String nextPageUrl = "https://www.geschichtewiki.wien.gv.at" + nextPageHref;
                scrapeCategory(nextPageUrl);
            }
        }
    }

    public void search() {
        try {
            this.scrapeCategory(eventSeed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ViennaHistoryWikiEventObject extractEventInfos(String url, String text) {
        try {
            Document doc = Jsoup.connect(url).get();

            ViennaHistoryWikiEventObject wikiObject = new ViennaHistoryWikiEventObject();
            wikiObject.setUrl(url);
            wikiObject.setName(text);

            // Extract all links from <p> tags
            wikiObject.setLinks(manualExtractorService.getLinks(doc));

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
                            wikiObject.setViennaHistoryWikiId(Optional.of(value));
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
            System.out.println(wikiObject);
            return wikiObject;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
