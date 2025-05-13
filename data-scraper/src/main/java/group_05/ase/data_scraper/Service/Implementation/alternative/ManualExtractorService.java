package group_05.ase.data_scraper.Service.Implementation.alternative;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ManualExtractorService {

    public String getNextPageUrl(Document doc, String category) {
        Element nextLink = doc.selectFirst("a[title=\"Kategorie:"+category+"\"]:contains(nächste Seite)");
        return nextLink != null ? nextLink.absUrl("href") : null;
    }

    public List<String> getLinks(Document doc) {
        Elements links = doc.select("p a[href]");
        List<String> validLinks = new ArrayList<>();

        for (Element link : links) {
            String linkHref = link.attr("abs:href");
            if (linkHref.startsWith("https://www.geschichtewiki.wien.gv.at/")) {
                validLinks.add(linkHref);
            }
        }

        return validLinks;
    }

}
