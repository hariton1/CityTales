package group_05.ase.data_scraper.Entity.CustomRestAPIObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WikipediaResponse {
    public String batchcomplete;
    public Continue _continue;
    public Query query;
}
