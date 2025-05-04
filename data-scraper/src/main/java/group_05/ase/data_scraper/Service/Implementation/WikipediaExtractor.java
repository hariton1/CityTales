package group_05.ase.data_scraper.Service.Implementation;

import group_05.ase.data_scraper.Service.Interface.IWikipediaExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WikipediaExtractor implements IWikipediaExtractor {

    public List<String> extractLinks(String url) {
        System.out.println("ur: " + url);
        List<String> fullUrls = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href^=/wiki/]");

            for (Element link : links) {
                String href = link.attr("href");
                if (href.startsWith("/wiki/")) {
                    String fullUrl = "https://en.wikipedia.org" + href;
                    fullUrls.add(fullUrl);
                }
            }
        } catch (Exception e) {
            System.out.println("Check later: " + url);
        }

        return fullUrls;
    }

    public String extractBodyContent(String url) throws IOException {
        StringBuilder bodyContent = new StringBuilder();
        // Connect to the URL and fetch the document
        Document doc = Jsoup.connect(url).get();

        // Select the div with id 'bodyContent'
        Element bodyDiv = doc.getElementById("bodyContent");

        // Extract and concatenate all <p> tag contents within that div
        if (bodyDiv != null) {
            for (Element paragraph : bodyDiv.select("p")) {
                bodyContent.append(paragraph.text()).append("\n");
            }
        } else {
            System.out.println("No bodyContent div found for: " + url);
        }

        return bodyContent.toString().trim();
    }
}
