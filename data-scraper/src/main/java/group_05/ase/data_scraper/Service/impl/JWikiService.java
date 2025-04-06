package group_05.ase.data_scraper.Service.impl;

import group_05.ase.data_scraper.Service.IJWikiDataService;
import io.github.fastily.jwiki.core.Wiki;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JWikiService implements IJWikiDataService {

    private final int searchDepth = 10;
    private Wiki wiki;

    // Just Testing
    public JWikiService() {
        this.wiki = new Wiki.Builder().build();
    }

    public String setUp() {
        return wiki.getPageText("Vienna");
    }

    public List<String> getCategories(String page) {
        return wiki.getCategoriesOnPage(page);
    }

    // actual stuff i need
    public List<String> getPageNames() {
        return wiki.search("Vienna",searchDepth);
    }
}
