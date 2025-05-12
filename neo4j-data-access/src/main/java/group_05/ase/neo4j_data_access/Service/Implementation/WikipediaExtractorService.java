package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Service.Interface.IWikipediaExtractorService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class WikipediaExtractorService implements IWikipediaExtractorService {
    public String getFirstParagraph(String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            // Select the first paragraph within the main content area
            Element firstParagraph = doc.select("div.mw-parser-output > p").first();

            if (firstParagraph != null) {
                return firstParagraph.text();
            } else {
                return "No content found in the first paragraph.";
            }
        } catch (Exception e) {
            return "Error retrieving the Wikipedia article.";
        }
    }
}
