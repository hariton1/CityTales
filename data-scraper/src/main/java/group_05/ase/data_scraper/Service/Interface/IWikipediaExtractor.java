package group_05.ase.data_scraper.Service.Interface;

import java.io.IOException;
import java.util.List;

public interface IWikipediaExtractor {
    List<String> extractLinks(String url);
    String extractBodyContent(String url) throws IOException;
}
