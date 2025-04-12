package group_05.ase.data_scraper.Entity.Json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContinueInfo {
    public String gblcontinue;

    @JsonProperty("continue")
    public String continueVal;
}
