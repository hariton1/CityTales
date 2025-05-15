package group_05.ase.data_scraper.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ViennaHistoryWikiEventObject {
    private String url;
    private String name;
    private Optional<String> typeOfEvent = Optional.empty();
    private Optional<String> dateFrom = Optional.empty();
    private Optional<String> dateTo = Optional.empty();
    private Optional<String> topic = Optional.empty();
    private Optional<String> organizer = Optional.empty();
    private Optional<String> participantCount = Optional.empty();
    private Optional<Boolean> violence = Optional.empty();
    private Optional<String> viennaHistoryWikiId = Optional.empty();
    private Optional<String> gnd = Optional.empty();
    private Optional<String> wikidataId = Optional.empty();
    private Optional<String> seeAlso = Optional.empty();
    private Optional<String> resource = Optional.empty();
    private List<String> links = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Optional<String> getTypeOfEvent() { return typeOfEvent; }
    public void setTypeOfEvent(Optional<String> typeOfEvent) { this.typeOfEvent = typeOfEvent; }

    public Optional<String> getDateFrom() { return dateFrom; }
    public void setDateFrom(Optional<String> dateFrom) { this.dateFrom = dateFrom; }

    public Optional<String> getDateTo() { return dateTo; }
    public void setDateTo(Optional<String> dateTo) { this.dateTo = dateTo; }

    public Optional<String> getTopic() { return topic; }
    public void setTopic(Optional<String> topic) { this.topic = topic; }

    public Optional<String> getOrganizer() { return organizer; }
    public void setOrganizer(Optional<String> organizer) { this.organizer = organizer; }

    public Optional<String> getParticipantCount() { return participantCount; }
    public void setParticipantCount(Optional<String> participantCount) { this.participantCount = participantCount; }

    public Optional<Boolean> getViolence() { return violence; }
    public void setViolence(Optional<Boolean> violence) { this.violence = violence; }

    public Optional<String> getViennaHistoryWikiId() { return viennaHistoryWikiId; }
    public void setViennaHistoryWikiId(Optional<String> viennaHistoryWikiId) { this.viennaHistoryWikiId = viennaHistoryWikiId; }

    public Optional<String> getGnd() { return gnd; }
    public void setGnd(Optional<String> gnd) { this.gnd = gnd; }

    public Optional<String> getWikidataId() { return wikidataId; }
    public void setWikidataId(Optional<String> wikidataId) { this.wikidataId = wikidataId; }

    public Optional<String> getSeeAlso() { return seeAlso; }
    public void setSeeAlso(Optional<String> seeAlso) { this.seeAlso = seeAlso; }

    public Optional<String> getResource() { return resource; }
    public void setResource(Optional<String> resource) { this.resource = resource; }

    public List<String> getLinks() { return links; }
    public void setLinks(List<String> links) { this.links = links; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    @Override
    public String toString() {
        return "ViennaHistoryWikiEventObject {\n" +
                "  url=" + url + ",\n" +
                "  name=" + name + ",\n" +
                "  typeOfEvent=" + typeOfEvent + ",\n" +
                "  dateFrom=" + dateFrom + ",\n" +
                "  dateTo=" + dateTo + ",\n" +
                "  topic=" + topic + ",\n" +
                "  organizer=" + organizer + ",\n" +
                "  participantCount=" + participantCount + ",\n" +
                "  violence=" + violence + ",\n" +
                "  viennaHistoryWikiId=" + viennaHistoryWikiId + ",\n" +
                "  gnd=" + gnd + ",\n" +
                "  wikidataId=" + wikidataId + ",\n" +
                "  seeAlso=" + seeAlso + ",\n" +
                "  resource=" + resource + "\n" +
                "  links=" + links + "\n" +
                "  imageUrls=" + imageUrls + "\n" +
                '}';
    }
}
