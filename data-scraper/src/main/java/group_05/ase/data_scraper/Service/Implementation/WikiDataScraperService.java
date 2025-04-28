package group_05.ase.data_scraper.Service.Implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.data_scraper.Entity.Json.Page;
import group_05.ase.data_scraper.Entity.Json.Root;
import group_05.ase.data_scraper.Entity.WikiDataObject;
import group_05.ase.data_scraper.Service.Interface.IWikiDataScraperService;
import group_05.ase.data_scraper.Service.WikiDataConsts.WikiDataConsts;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


@Service
public class WikiDataScraperService implements IWikiDataScraperService {
    private final String WIKIDATA_API_URL = "https://www.wikidata.org/w/api.php";
    private final WikiDataService wikiDataService;

    public WikiDataScraperService(WikiDataService wikiDataService) {
        this.wikiDataService = wikiDataService;
    }

    public void batchSearch(int batchSize, int iterations) {

        String continueToken = "";
        System.out.println(continueToken);
        for (int i = 0; i < iterations;i++) {
            Root batch = getWhatLinksHere(batchSize,continueToken);
            for (Map.Entry<String, Page> entry:batch.query.pages.entrySet()) {
                String wikiDataId = entry.getValue().title;
                WikiDataObject wDO = wikiDataService.extractWikiDataObjectFromEntityId(wikiDataId);
                wDO.setPageId(entry.getValue().pageid);
                System.out.println(wDO);

                // TODO: persist in Neo4JDB
            }
            continueToken = batch.continueInfo.gblcontinue;
        }
    }

    public Root getWhatLinksHere(int limit, String continueToken) {
        String urlString = WIKIDATA_API_URL
                + "?action=query&format=json&generator=backlinks"
                + "&gblnamespace=0&gbllimit=" + limit
                + "&prop=info&gbltitle=Property:" + WikiDataConsts.VIENNA_HISTORY_WIKI_ID;

        if (continueToken != "") {
            urlString = urlString
                + "&continue=||&gblcontinue=" + continueToken
                    + "&gbllimit=" + limit;
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            String responseContent = getResponseContent(conn).toString();

            ObjectMapper mapper = new ObjectMapper();
            Root root = mapper.readValue(responseContent, Root.class);
            return root;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static StringBuilder getResponseContent(HttpURLConnection conn) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder responseContent = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            responseContent.append(inputLine);
        }

        in.close();
        conn.disconnect();
        return responseContent;
    }
}
