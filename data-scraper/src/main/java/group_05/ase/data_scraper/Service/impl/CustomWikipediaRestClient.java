package group_05.ase.data_scraper.Service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.data_scraper.Entity.CustomRestAPIObjects.SearchResult;
import group_05.ase.data_scraper.Entity.CustomRestAPIObjects.WikipediaResponse;
import group_05.ase.data_scraper.Service.ICustomWikipediaRestClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomWikipediaRestClient implements ICustomWikipediaRestClient {
    private final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php";
    public WikipediaResponse searchOnce(String query, int offset) {
        try {
            HttpURLConnection conn = getHttpURLConnection(query, offset);
            String responseContent = getResponseContent(conn).toString();

            return deserializeJSONResponse(responseContent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<SearchResult> searchBatch(String query, int x, int startAt) {
        List<SearchResult> allResults = new ArrayList<>();

        for (int i = 0; i < x; i++) {
            int currentOffset = startAt + (i * 10); // Wikipedia returns 10 results per page
            WikipediaResponse response = searchOnce(query, currentOffset); // You can also use searchX

            if (response != null && response.query != null && response.query.search != null) {
                allResults.addAll(response.query.search);
            }
        }

        return allResults;
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

    @NotNull
    private HttpURLConnection getHttpURLConnection(String query, int offset) throws IOException {
        String urlString = WIKIPEDIA_API_URL +
                "?action=query" +
                "&list=search" +
                "&srsearch=" + query +
                "&format=json" +
                "&sroffset=" + offset;

        URL url = new URL(urlString);
        System.out.println("url: " +url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        return conn;
    }

    public WikipediaResponse deserializeJSONResponse(String responseContent) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseContent, WikipediaResponse.class);
    }
}
