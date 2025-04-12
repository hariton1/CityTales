package group_05.ase.data_scraper.Entity.CustomRestAPIObjects.SearchFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {
    public int pageid;
    public String title;
    public String snippet;

    @Override
    public String toString() {
        return "PageID: " + pageid + ", Title: " + title + ", Snippet: " + snippet;
    }
}
