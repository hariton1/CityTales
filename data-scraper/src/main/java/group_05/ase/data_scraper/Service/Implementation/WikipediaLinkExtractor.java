package group_05.ase.data_scraper.Service.Implementation;

import group_05.ase.data_scraper.Service.Interface.IWikipediaLinkExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WikipediaLinkExtractor implements IWikipediaLinkExtractor {

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
}
