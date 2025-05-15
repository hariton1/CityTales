package group_05.ase.data_scraper.Old_Scraper.Service.Interface;

import java.util.List;

public interface IWikipediaLinkExtractor {
    List<String> extractLinks(String url);
}
