package group_05.ase.data_scraper.Service.persons;

import group_05.ase.data_scraper.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.data_scraper.Service.embeddings.OpenAiService;
import group_05.ase.data_scraper.Service.general.ContentService;
import group_05.ase.data_scraper.Service.general.ManualExtractorService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PersonService {

    public String personsSeeds = "https://www.geschichtewiki.wien.gv.at/Kategorie:Personen";
    private final PersonRepository personRepository;
    private final ManualExtractorService manualExtractorService;
    private final ContentService contentService;
    private final OpenAiService openAiService;



    public PersonService(PersonRepository wikiPersonPersistenceService, ManualExtractorService manualExtractorService, ContentService contentService, OpenAiService openAiService) {
        this.personRepository = wikiPersonPersistenceService;
        this.manualExtractorService = manualExtractorService;
        this.contentService = contentService;
        this.openAiService = openAiService;
    }

    public void search(int limit) {
        String currentUrl = personsSeeds;
        int totalLinks = 0;
        int persistedCount = 0;

        while (currentUrl != null && persistedCount < limit) {
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
                                .limit(20)
                                .parallel()
                                .map(link -> {
                                    return extractPersonInfos(link.attr("abs:href"), link.text());
                                })
                                .filter(obj -> obj != null)
                                .toList();

                        entries.addAll(pageEntries);
                        totalLinks += pageEntries.size();


                        totalLinks += pageLinkCount;

                        for (ViennaHistoryWikiPersonObject wgwo:entries) {
                            if (persistedCount >= limit) {
                                return;
                            }
                            personRepository.persistViennaHistoryWikiPersonObject(wgwo);
                            persistedCount++;
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
        }
    }

    private ViennaHistoryWikiPersonObject extractPersonInfos(String url, String text) {
        try {
            Document doc = Jsoup.connect(url).get();

            ViennaHistoryWikiPersonObject wikiObject = new ViennaHistoryWikiPersonObject();
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
                        if (key.equals("PersonennameName der Person im Format Nachname, Vorname")) {
                            wikiObject.setPersonName(Optional.of(value));
                        } else if (key.equals("Abweichende NamensformAlternative Formen des Namens wie z.B. Pseudonyme oder Mädchennamen im Format Nachname, Vorname")) {
                            wikiObject.setAlternativeName(Optional.of(value));
                        } else if (key.equals("TitelAkademische Titel (abgekürzt), Amtstitel, Adelstitel")) {
                            wikiObject.setAlternativeName(Optional.of(value));
                        } else if (key.equals("Geschlecht")) {
                            wikiObject.setSex(Optional.of(value));
                        } else if (key.equals("Wien Geschichte WikiIdentifier/Persistenter URL zur Seite ᵖ")) {
                            wikiObject.setViennaHistoryWikiId(Integer.parseInt(value));
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

            // Embeddings
            if (wikiObject.getContentGerman() != null && !wikiObject.getContentGerman().isEmpty()) {
                System.out.println("Content!: " + wikiObject.getContentGerman());

                float[] embedding = openAiService.getEmbedding(wikiObject.getContentGerman());
                personRepository.persistEmbedding(embedding, wikiObject.getViennaHistoryWikiId());
                wikiObject.setContentEnglish("");
            }

            return wikiObject;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
