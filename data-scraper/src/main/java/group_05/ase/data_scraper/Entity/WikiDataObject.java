package group_05.ase.data_scraper.Entity;

public class WikiDataObject {
    private String wikiDataId;
    private String wikiName;
    private String shortDescription;
    private String location;

    public WikiDataObject() {
    }

    public String getWikiDataId() {
        return wikiDataId;
    }

    public void setWikiDataId(String wikiDataId) {
        this.wikiDataId = wikiDataId;
    }

    public String getWikiName() {
        return wikiName;
    }

    public void setWikiName(String wikiName) {
        this.wikiName = wikiName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "WikiDataObject{" +
                "wikiDataId='" + wikiDataId + '\'' +
                ", wikiName='" + wikiName + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
