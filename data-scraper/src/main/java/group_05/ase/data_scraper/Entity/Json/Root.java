package group_05.ase.data_scraper.Entity.Json;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Root {
    public String batchcomplete;

    @JsonProperty("continue")
    public ContinueInfo continueInfo;

    public Query query;
}
