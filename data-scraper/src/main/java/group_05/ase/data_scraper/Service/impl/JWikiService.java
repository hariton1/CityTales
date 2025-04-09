package group_05.ase.data_scraper.Service.impl;

import group_05.ase.data_scraper.Service.IJWikiDataService;
import io.github.fastily.jwiki.core.Wiki;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JWikiService implements IJWikiDataService {

    private final int searchDepth = 100_000;
    private Wiki wiki;

    public JWikiService() {
        this.wiki = new Wiki.Builder().build();
    }

    // Just Trying different Methods

    public String getPageText() {
        return wiki.getPageText("Vienna");
    }

    public List<String> getCategories(String page) {
        return wiki.getCategoriesOnPage(page);
    }

    public List<String> getPageNames() {
        return wiki.search("Vienna",searchDepth);
    }

    public List<String> getLinkedPagesNames(String name) {
        return wiki.whatLinksHere(name);
    }

    // Recursive method to search linked pages containing "Vienna" in their name
    public Map<String, String> recursiveSearch(String seed, int depth, Set<String> visitedPages, Map<String, String> linksMap, int maxDepth, int maxResults) {
        if (depth > maxDepth || linksMap.size() >= maxResults) {
            return linksMap;
        }

        if (visitedPages.contains(seed)) {
            return linksMap;
        }

        visitedPages.add(seed);
        List<String> linkedPageNames = getLinkedPagesNames(seed);

        for (String page : linkedPageNames) {
            if (page.toLowerCase().contains("vienna")) {
                linksMap.put(seed, page);
                recursiveSearch(page, depth + 1, visitedPages, linksMap, maxDepth, maxResults);
            }
        }

        return linksMap;
    }

    public Map<String, String> startRecursiveSearch(String seed) {
        Set<String> visitedPages = new HashSet<>();
        Map<String, String> linksMap = new HashMap<>();
        recursiveSearch(seed, 0, visitedPages, linksMap, 3, 10);
        return linksMap;
    }
}
