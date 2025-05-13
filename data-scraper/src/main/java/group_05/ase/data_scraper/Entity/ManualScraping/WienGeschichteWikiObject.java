package group_05.ase.data_scraper.Entity.ManualScraping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WienGeschichteWikiObject {

    private String url;
    private String name;
    private Optional<String> buildingType = Optional.empty();
    private Optional<String> dateFrom = Optional.empty();
    private Optional<String> dateTo = Optional.empty();
    private Optional<String> otherName = Optional.empty();
    private Optional<String> previousName = Optional.empty();
    private Optional<String> namedAfter = Optional.empty();
    private Optional<String> entryNumber = Optional.empty();
    private Optional<String> architect = Optional.empty();
    private Optional<String> famousResidents = Optional.empty();
    private Optional<String> wienGeschichteWikiId = Optional.empty();
    private Optional<String> gnd = Optional.empty();
    private Optional<String> wikidataId = Optional.empty();
    private Optional<String> seeAlso = Optional.empty();
    private Optional<String> resource = Optional.empty();
    private Optional<Double> latitude = Optional.empty();
    private Optional<Double> longitude = Optional.empty();

    private List<String> links = new ArrayList<>();

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getName() { return name;}
    public void setName(String name) { this.name = name; }

    public Optional<String> getBuildingType() { return buildingType; }
    public void setBuildingType(Optional<String> buildingType) { this.buildingType = buildingType; }

    public Optional<String> getDateFrom() { return dateFrom; }
    public void setDateFrom(Optional<String> dateFrom) { this.dateFrom = dateFrom; }

    public Optional<String> getDateTo() { return dateTo; }
    public void setDateTo(Optional<String> dateTo) { this.dateTo = dateTo; }

    public Optional<String> getOtherName() { return otherName; }
    public void setOtherName(Optional<String> otherName) { this.otherName = otherName; }

    public Optional<String> getPreviousName() { return previousName; }
    public void setPreviousName(Optional<String> previousName) { this.previousName = previousName; }

    public Optional<String> getNamedAfter() { return namedAfter; }
    public void setNamedAfter(Optional<String> namedAfter) { this.namedAfter = namedAfter; }

    public Optional<String> getEntryNumber() { return entryNumber; }
    public void setEntryNumber(Optional<String> entryNumber) { this.entryNumber = entryNumber; }

    public Optional<String> getArchitect() { return architect; }
    public void setArchitect(Optional<String> architect) { this.architect = architect; }

    public Optional<String> getFamousResidents() { return famousResidents; }
    public void setFamousResidents(Optional<String> famousResidents) { this.famousResidents = famousResidents; }

    public Optional<String> getWienGeschichteWikiId() { return wienGeschichteWikiId; }
    public void setWienGeschichteWikiId(Optional<String> wienGeschichteWikiId) { this.wienGeschichteWikiId = wienGeschichteWikiId; }

    public Optional<String> getGnd() { return gnd; }
    public void setGnd(Optional<String> gnd) { this.gnd = gnd; }

    public Optional<String> getWikidataId() { return wikidataId; }
    public void setWikidataId(Optional<String> wikidataId) { this.wikidataId = wikidataId; }

    public Optional<String> getSeeAlso() { return seeAlso; }
    public void setSeeAlso(Optional<String> seeAlso) { this.seeAlso = seeAlso; }

    public Optional<String> getResource() { return resource; }
    public void setResource(Optional<String> resource) { this.resource = resource; }

    public Optional<Double> getLatitude() { return latitude; }

    public void setLatitude(Optional<Double> latitude) { this.latitude = latitude; }

    public Optional<Double> getLongitude() { return longitude; }

    public void setLongitude(Optional<Double> longitude) { this.longitude = longitude; }

    public List<String> getLinks() { return links; }
    public void setLinks(List<String> links) { this.links = links; }

    @Override
    public String toString() {
        return "WienGeschichteWikiObject {\n" +
                "  url=" + url + ",\n" +
                "  name=" + name + ",\n" +
                "  buildingType=" + buildingType + ",\n" +
                "  dateFrom=" + dateFrom + ",\n" +
                "  dateTo=" + dateTo + ",\n" +
                "  otherName=" + otherName + ",\n" +
                "  previousName=" + previousName + ",\n" +
                "  namedAfter=" + namedAfter + ",\n" +
                "  entryNumber=" + entryNumber + ",\n" +
                "  architect=" + architect + ",\n" +
                "  famousResidents=" + famousResidents + ",\n" +
                "  wienGeschichteWikiId=" + wienGeschichteWikiId + ",\n" +
                "  gnd=" + gnd + ",\n" +
                "  wikidataId=" + wikidataId + ",\n" +
                "  seeAlso=" + seeAlso + ",\n" +
                "  resource=" + resource + "\n" +
                "  latitude=" + latitude + ",\n" +
                "  longitude=" + longitude + ",\n" +
                "  links=" + links + "\n" +
                '}';
    }
}
