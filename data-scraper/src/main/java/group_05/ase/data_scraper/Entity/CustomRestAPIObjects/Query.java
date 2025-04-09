package group_05.ase.data_scraper.Entity.CustomRestAPIObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Query {
    public List<SearchResult> search;
}
