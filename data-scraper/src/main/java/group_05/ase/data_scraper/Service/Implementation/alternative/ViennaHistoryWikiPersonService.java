package group_05.ase.data_scraper.Service.Implementation.alternative;

import group_05.ase.data_scraper.Entity.ManualScraping.ViennaHistoryWikiPersonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ViennaHistoryWikiPersonService {

    public String personsSeeds = "https://www.geschichtewiki.wien.gv.at/Kategorie:Personen";
    private final ViennaHistoryWikiPersonPersistenceService wikiPersonPersistenceService;
    private final ManualExtractorService manualExtractorService;

    public ViennaHistoryWikiPersonService(ViennaHistoryWikiPersonPersistenceService wikiPersonPersistenceService, ManualExtractorService manualExtractorService) {
        this.wikiPersonPersistenceService = wikiPersonPersistenceService;
        this.manualExtractorService = manualExtractorService;
    }

    public void search() {
        String currentUrl = personsSeeds;
        int totalLinks = 0;
        int breaker = 0;

        while (currentUrl != null && breaker <= 24) {
            System.out.println("persons: " + breaker +"/24");
            List<ViennaHistoryWikiPersonObject> entries = new ArrayList<>();
            try {

                Document doc = Jsoup.connect(currentUrl).get();
                Element outerDiv = doc.selectFirst("div.mw-content-ltr[lang=de][dir=ltr]");

                if (outerDiv != null) {
                    Element categoryDiv = outerDiv.selectFirst("div.mw-category.mw-category-columns");

                    if (categoryDiv != null) {
                        Elements links = categoryDiv.select("a");
                        int pageLinkCount = 0;

                        List<ViennaHistoryWikiPersonObject> pageEntries = links.stream()
                                .limit(200)
                                .parallel()
                                .map(link -> {
                                    return extractBuildingInfos(link.attr("abs:href"), link.text());
                                })
                                .filter(obj -> obj != null)
                                .toList();

                        entries.addAll(pageEntries);
                        totalLinks += pageEntries.size();


                        totalLinks += pageLinkCount;
                        System.out.println("Total links found on this page: " + pageLinkCount);

                        for (ViennaHistoryWikiPersonObject wgwo:entries) {
                            wikiPersonPersistenceService.persistViennaHistoryWikiPersonObject(wgwo);
                        }
                    } else {
                        System.out.println("Category div not found on the current page.");
                    }
                } else {
                    System.out.println("Outer mw-content-ltr div not found on the current page.");
                }

                String nextPageUrl = manualExtractorService.getNextPageUrl(doc,"Personen");

                if (nextPageUrl == null) {
                    System.out.println("No more pages to fetch. Status: " + totalLinks);
                    break;
                } else {
                    currentUrl = nextPageUrl;
                }

                System.out.println("Cumulative total of links found: " + totalLinks);

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            breaker+= 1;
        }
    }

    private ViennaHistoryWikiPersonObject extractBuildingInfos(String url, String text) {
        try {
            Document doc = Jsoup.connect(url).get();

            ViennaHistoryWikiPersonObject wikiObject = new ViennaHistoryWikiPersonObject();
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
                        if (key.equals("PersonennameName der Person im Format Nachname, Vorname")) {
                            wikiObject.setPersonName(Optional.of(value));
                        } else if (key.equals("Abweichende NamensformAlternative Formen des Namens wie z.B. Pseudonyme oder Mädchennamen im Format Nachname, Vorname")) {
                            wikiObject.setAlternativeName(Optional.of(value));
                        } else if (key.equals("TitelAkademische Titel (abgekürzt), Amtstitel, Adelstitel")) {
                            wikiObject.setAlternativeName(Optional.of(value));
                        } else if (key.equals("Geschlecht")) {
                            wikiObject.setSex(Optional.of(value));
                        } else if (key.equals("Wien Geschichte WikiIdentifier/Persistenter URL zur Seite ᵖ")) {
                            wikiObject.setViennaHistoryWikiId(Optional.of(value));
                        } else if (key.equals("GNDGemeindsame Normdatei")) {
                            wikiObject.setGnd(Optional.of(value));
                        } else if (key.equals("Wikidata")) {
                            wikiObject.setWikidataId(Optional.of(value));
                        } else if (key.equals("GeburtsdatumDatum der Geburt")) {
                            wikiObject.setBirthDate(Optional.of(value));
                        } else if (key.equals("GeburtsortOrt der Geburt")) {
                            wikiObject.setBirthPlace(Optional.of(value));
                        } else if (key.equals("SterbedatumSterbedatum")) {
                            wikiObject.setDeathDate(Optional.of(value));
                        } else if (key.equals("SterbeortSterbeort")) {
                            wikiObject.setDeathPlace(Optional.of(value));
                        } else if (key.equals("BerufBeruf")) {
                            wikiObject.setJobs(Optional.of(value));
                        } else if (key.equals("ParteizugehörigkeitAngabe der Partei (bei PolitikerInnen)")) {
                            wikiObject.setPoliticalLinkage(Optional.of(value));
                        } else if (key.equals("EreignisEreignis, mit dem die Person in Verbindung gebracht wird")) {
                            wikiObject.setDeathPlace(Optional.of(value));
                        } else if (key.equals("Nachlass/Vorlass")) {
                            wikiObject.setEstate(Optional.of(value));
                        } else if (key.equals("Siehe auchVerweist auf andere Objekte im Wiki ᵖ")) {
                            wikiObject.setSeeAlso(Optional.of(value));
                        } else if (key.equals("RessourceUrsprüngliche Ressource ᵖ")) {
                            wikiObject.setResource(Optional.of(value));
                        }
                    }
                }
            }
            return wikiObject;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
