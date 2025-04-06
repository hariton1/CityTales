package group_05.ase.data_scraper.Entity;

import java.util.ArrayList;
import java.util.List;

public class WikiDataObject {
    private String wikiDataId;
    private String wikiName;
    private String shortDescription;
    private String location;
    private List<String> instanceOf;

    public WikiDataObject() {
        this.instanceOf = new ArrayList<>();
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

    public List<String> getInstanceOf() {
        return instanceOf;
    }

    public void setInstanceOf(List<String> instanceOf) {
        this.instanceOf = instanceOf;
    }

    public void addToInstanceOf(String s) {
        if (this.instanceOf == null) {
            this.instanceOf = new ArrayList<>();
        }
        this.instanceOf.add(s);
    }

    @Override
    public String toString() {
        return "WikiDataObject{" +
                "wikiDataId='" + wikiDataId + '\'' +
                ", wikiName='" + wikiName + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", location='" + location + '\'' +
                ", instanceOf=" + instanceOf +
                '}';
    }
}
