package group_05.ase.data_scraper.Entity.CustomRestAPIObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Continue {
    public int sroffset;
    public String _continue;
}
