package group_05.ase.data_scraper.Entity.CustomRestAPIObjects.SearchFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Continue {
    public String gblcontinue;
    public String _continue;
}
