package group_05.ase.orchestrator.dto;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ViennaHistoryWikiBuildingObject {
    private int viennaHistoryWikiId;
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
    private Optional<String> gnd = Optional.empty();
    private Optional<String> wikidataId = Optional.empty();
    private Optional<String> seeAlso = Optional.empty();
    private Optional<String> resource = Optional.empty();
    private Optional<Double> latitude = Optional.empty();
    private Optional<Double> longitude = Optional.empty();

    private List<String> links = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

//    private List<ViennaHistoryWikiBuildingObject> relatedBuildings = new ArrayList<>();
//    private List<ViennaHistoryWikiPersonObject> relatedPersons = new ArrayList<>();
//    private List<ViennaHistoryWikiEventObject> relatedEvents = new ArrayList<>();

    private String contentGerman;
    private String contentEnglish;


    public ViennaHistoryWikiBuildingObject(int viennaHistoryWikiId, String url, String name, Optional<String> buildingType, Optional<String> dateFrom, Optional<String> dateTo, Optional<String> otherName, Optional<String> previousName, Optional<String> namedAfter, Optional<String> entryNumber, Optional<String> architect, Optional<String> famousResidents, Optional<String> gnd, Optional<String> wikidataId, Optional<String> seeAlso, Optional<String> resource, Optional<Double> latitude, Optional<Double> longitude, List<String> links, List<String> imageUrls, String contentGerman, String contentEnglish) {
        this.viennaHistoryWikiId = viennaHistoryWikiId;
        this.url = url;
        this.name = name;
        this.buildingType = buildingType;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.otherName = otherName;
        this.previousName = previousName;
        this.namedAfter = namedAfter;
        this.entryNumber = entryNumber;
        this.architect = architect;
        this.famousResidents = famousResidents;
        this.gnd = gnd;
        this.wikidataId = wikidataId;
        this.seeAlso = seeAlso;
        this.resource = resource;
        this.latitude = latitude;
        this.longitude = longitude;
        this.links = links;
        this.imageUrls = imageUrls;
        this.contentGerman = contentGerman;
        this.contentEnglish = contentEnglish;
    }

    public Optional<String> getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(Optional<String> entryNumber) {
        this.entryNumber = entryNumber;
    }

    public int getViennaHistoryWikiId() {
        return viennaHistoryWikiId;
    }

    public void setViennaHistoryWikiId(int viennaHistoryWikiId) {
        this.viennaHistoryWikiId = viennaHistoryWikiId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(Optional<String> buildingType) {
        this.buildingType = buildingType;
    }

    public Optional<String> getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Optional<String> dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Optional<String> getDateTo() {
        return dateTo;
    }

    public void setDateTo(Optional<String> dateTo) {
        this.dateTo = dateTo;
    }

    public Optional<String> getOtherName() {
        return otherName;
    }

    public void setOtherName(Optional<String> otherName) {
        this.otherName = otherName;
    }

    public Optional<String> getPreviousName() {
        return previousName;
    }

    public void setPreviousName(Optional<String> previousName) {
        this.previousName = previousName;
    }

    public Optional<String> getNamedAfter() {
        return namedAfter;
    }

    public void setNamedAfter(Optional<String> namedAfter) {
        this.namedAfter = namedAfter;
    }

    public Optional<String> getArchitect() {
        return architect;
    }

    public void setArchitect(Optional<String> architect) {
        this.architect = architect;
    }

    public Optional<String> getFamousResidents() {
        return famousResidents;
    }

    public void setFamousResidents(Optional<String> famousResidents) {
        this.famousResidents = famousResidents;
    }

    public Optional<String> getGnd() {
        return gnd;
    }

    public void setGnd(Optional<String> gnd) {
        this.gnd = gnd;
    }

    public Optional<String> getWikidataId() {
        return wikidataId;
    }

    public void setWikidataId(Optional<String> wikidataId) {
        this.wikidataId = wikidataId;
    }

    public Optional<String> getSeeAlso() {
        return seeAlso;
    }

    public void setSeeAlso(Optional<String> seeAlso) {
        this.seeAlso = seeAlso;
    }

    public Optional<String> getResource() {
        return resource;
    }

    public void setResource(Optional<String> resource) {
        this.resource = resource;
    }

    public Optional<Double> getLatitude() {
        return latitude;
    }

    public void setLatitude(Optional<Double> latitude) {
        this.latitude = latitude;
    }

    public Optional<Double> getLongitude() {
        return longitude;
    }

    public void setLongitude(Optional<Double> longitude) {
        this.longitude = longitude;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getContentGerman() {
        return contentGerman;
    }

    public void setContentGerman(String contentGerman) {
        this.contentGerman = contentGerman;
    }

    public String getContentEnglish() {
        return contentEnglish;
    }

    public void setContentEnglish(String contentEnglish) {
        this.contentEnglish = contentEnglish;
    }
}