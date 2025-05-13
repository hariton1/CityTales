package group_05.ase.data_scraper.Service.Implementation;

import group_05.ase.data_scraper.Entity.ManualScraping.ViennaHistoryWikiBuildingObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ViennaHistoryWikiBuildingService {

    public String buildingSeeds = "https://www.geschichtewiki.wien.gv.at/Kategorie:Geb%C3%A4ude";
    private List<ViennaHistoryWikiBuildingObject> entries = new ArrayList<>();
    private final ViennaHistoryWikiBuildingPersistenceService wienGeschichteWikiPersistenceService;
    private final ManualExtractorService manualExtractorService;


    public ViennaHistoryWikiBuildingService(ViennaHistoryWikiBuildingPersistenceService wienGeschichteWikiPersistenceService, ManualExtractorService manualExtractorService) {
        this.wienGeschichteWikiPersistenceService = wienGeschichteWikiPersistenceService;
        this.manualExtractorService = manualExtractorService;
    }
    public void search() {
        String currentUrl = buildingSeeds;
        int totalLinks = 0;
        int breaker = 0;

        while (currentUrl != null) {
            try {

                Document doc = Jsoup.connect(currentUrl).get();
                Element outerDiv = doc.selectFirst("div.mw-content-ltr[lang=de][dir=ltr]");

                if (outerDiv != null) {
                    Element categoryDiv = outerDiv.selectFirst("div.mw-category.mw-category-columns");

                    if (categoryDiv != null) {
                        Elements links = categoryDiv.select("a");
                        int pageLinkCount = 0;

                        List<ViennaHistoryWikiBuildingObject> pageEntries = links.stream()
                                .limit(200)
                                .parallel()
                                .map(link -> {
                                    System.out.println("Href: " + link.attr("abs:href"));
                                    System.out.println("Text: " + link.text());
                                    System.out.println("------");
                                    return extractBuildingInfos(link.attr("abs:href"), link.text());
                                })
                                .filter(obj -> obj != null)
                                .toList();

                        entries.addAll(pageEntries);
                        totalLinks += pageEntries.size();


                        totalLinks += pageLinkCount;
                        System.out.println("Total links found on this page: " + pageLinkCount);

                        for (ViennaHistoryWikiBuildingObject wgwo:entries) {
                            wienGeschichteWikiPersistenceService.persistViennaHistoryWikiBuildingObject(wgwo);
                        }
                    } else {
                        System.out.println("Category div not found on the current page.");
                    }
                } else {
                    System.out.println("Outer mw-content-ltr div not found on the current page.");
                }

                String nextPageUrl = manualExtractorService.getNextPageUrl(doc,"Gebäude");

                if (nextPageUrl == null) {
                    System.out.println("No more pages to fetch. Status: " + totalLinks);
                    break;
                } else {
                    currentUrl = nextPageUrl;
                }
                breaker+=1;

                System.out.println("Cumulative total of links found: " + totalLinks);

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

       /* for (WienGeschichteWikiObject obj:entries) {
            System.out.println(obj.toString());
        }*/
    }

    private ViennaHistoryWikiBuildingObject extractBuildingInfos(String url, String text) {
        try {
            Document doc = Jsoup.connect(url).get();

            ViennaHistoryWikiBuildingObject wikiObject = new ViennaHistoryWikiBuildingObject();
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
                        if (key.equals("Art des Bauwerks")) {
                            wikiObject.setBuildingType(Optional.of(value));
                        } else if (key.equals("Datum vonDatum (oder Jahr) von")) {
                            wikiObject.setDateFrom(Optional.of(value));
                        } else if (key.equals("Datum bisDatum (oder Jahr) bis")) {
                            wikiObject.setDateTo(Optional.of(value));
                        } else if (key.equals("Andere BezeichnungAndere Bezeichnung für diesen Eintrag")) {
                            wikiObject.setOtherName(Optional.of(value));
                        } else if (key.equals("Frühere BezeichnungFrühere Bezeichnung für diesen Eintrag")) {
                            wikiObject.setPreviousName(Optional.of(value));
                        } else if (key.equals("Benannt nach")) {
                            wikiObject.setNamedAfter(Optional.of(value));
                        } else if (key.equals("Einlagezahl")) {
                            wikiObject.setEntryNumber(Optional.of(value));
                        } else if (key.equals("Architekt*inKünstler*in/Architekt*in ᵖ")) {
                            wikiObject.setArchitect(Optional.of(value));
                        } else if (key.equals("Prominente BewohnerWichtige Personen mit Bezug zum Objekt oder Bauwerk")) {
                            wikiObject.setFamousResidents(Optional.of(value));
                        } else if (key.equals("Wien Geschichte WikiIdentifier/Persistenter URL zur Seite ᵖ")) {
                            wikiObject.setWienGeschichteWikiId(Optional.of(value));
                        } else if (key.equals("GNDGemeindsame Normdatei")) {
                            wikiObject.setGnd(Optional.of(value));
                        } else if (key.equals("WikidataIDID von Wikidata")) {
                            wikiObject.setWikidataId(Optional.of(value));
                        } else if (key.equals("Siehe auchVerweist auf andere Objekte im Wiki ᵖ")) {
                            wikiObject.setSeeAlso(Optional.of(value));
                        } else if (key.equals("RessourceUrsprüngliche Ressource ᵖ")) {
                            wikiObject.setResource(Optional.of(value));
                        }
                    }
                }
            }

            populateCoordinates(doc,wikiObject);

            return wikiObject;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void populateCoordinates(Document doc, ViennaHistoryWikiBuildingObject obj) {
        Element mapDataDiv = doc.selectFirst("div.mobilemap div.mapdata");
        if (mapDataDiv != null) {
            String json = mapDataDiv.html();
            try {
                JSONObject mapData = new JSONObject(json);
                JSONArray locations = mapData.optJSONArray("locations");
                if (locations != null && locations.length() > 0) {
                    JSONObject location = locations.getJSONObject(0);
                    double lat = location.optDouble("lat", Double.NaN);
                    double lon = location.optDouble("lon", Double.NaN);
                    if (!Double.isNaN(lat) && !Double.isNaN(lon)) {
                        obj.setLatitude(Optional.of(lat));
                        obj.setLongitude(Optional.of(lon));
                    }
                }
            } catch (JSONException e) {
                System.err.println("Could not parse coordinates from JSON");
                e.printStackTrace();
            }
        }
    }
}
