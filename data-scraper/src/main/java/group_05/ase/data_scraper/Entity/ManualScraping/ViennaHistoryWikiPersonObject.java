package group_05.ase.data_scraper.Entity.ManualScraping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ViennaHistoryWikiPersonObject {

    private String url;
    private String name;
    private Optional<String> personName = Optional.empty();
    private Optional<String> alternativeName = Optional.empty();
    private Optional<String> titles = Optional.empty();
    private Optional<String> sex = Optional.empty();
    private Optional<String> viennaHistoryWikiId = Optional.empty();
    private Optional<String> gnd = Optional.empty();
    private Optional<String> wikidataId = Optional.empty();
    private Optional<String> birthDate = Optional.empty();
    private Optional<String> birthPlace = Optional.empty();
    private Optional<String> deathDate = Optional.empty();
    private Optional<String> deathPlace = Optional.empty();
    private Optional<String> jobs = Optional.empty();
    private Optional<String> politicalLinkage = Optional.empty();
    private Optional<String> event = Optional.empty();
    private Optional<String> estate = Optional.empty();
    private Optional<String> seeAlso = Optional.empty();
    private Optional<String> resource = Optional.empty();
    private List<String> links = new ArrayList<>();

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Optional<String> getPersonName() { return personName; }
    public void setPersonName(Optional<String> personName) { this.personName = personName; }

    public Optional<String> getAlternativeName() { return alternativeName; }
    public void setAlternativeName(Optional<String> alternativeName) { this.alternativeName = alternativeName; }

    public Optional<String> getTitles() { return titles; }
    public void setTitles(Optional<String> titles) { this.titles = titles; }

    public Optional<String> getSex() { return sex; }
    public void setSex(Optional<String> sex) { this.sex = sex; }

    public Optional<String> getViennaHistoryWikiId() { return viennaHistoryWikiId; }
    public void setViennaHistoryWikiId(Optional<String> viennaHistoryWikiId) { this.viennaHistoryWikiId = viennaHistoryWikiId; }

    public Optional<String> getGnd() { return gnd; }
    public void setGnd(Optional<String> gnd) { this.gnd = gnd; }

    public Optional<String> getWikidataId() { return wikidataId; }
    public void setWikidataId(Optional<String> wikidataId) { this.wikidataId = wikidataId; }

    public Optional<String> getBirthDate() { return birthDate; }
    public void setBirthDate(Optional<String> birthDate) { this.birthDate = birthDate; }

    public Optional<String> getBirthPlace() { return birthPlace; }
    public void setBirthPlace(Optional<String> birthPlace) { this.birthPlace = birthPlace; }

    public Optional<String> getDeathDate() { return deathDate; }
    public void setDeathDate(Optional<String> deathDate) { this.deathDate = deathDate; }

    public Optional<String> getDeathPlace() { return deathPlace; }
    public void setDeathPlace(Optional<String> deathPlace) { this.deathPlace = deathPlace; }

    public Optional<String> getJobs() { return jobs; }
    public void setJobs(Optional<String> jobs) { this.jobs = jobs; }

    public Optional<String> getPoliticalLinkage() { return politicalLinkage; }
    public void setPoliticalLinkage(Optional<String> politicalLinkage) { this.politicalLinkage = politicalLinkage; }

    public Optional<String> getEvent() { return event; }
    public void setEvent(Optional<String> event) { this.event = event; }

    public Optional<String> getEstate() { return estate; }
    public void setEstate(Optional<String> estate) { this.estate = estate; }

    public Optional<String> getSeeAlso() { return seeAlso; }
    public void setSeeAlso(Optional<String> seeAlso) { this.seeAlso = seeAlso; }

    public Optional<String> getResource() { return resource; }
    public void setResource(Optional<String> resource) { this.resource = resource; }

    public List<String> getLinks() { return links; }
    public void setLinks(List<String> links) { this.links = links; }

    @Override
    public String toString() {
        return "ViennaHistoryWikiPersonObject {\n" +
                "  url=" + url + ",\n" +
                "  name=" + name + ",\n" +
                "  personName=" + personName + ",\n" +
                "  alternativeName=" + alternativeName + ",\n" +
                "  titles=" + titles + ",\n" +
                "  sex=" + sex + ",\n" +
                "  viennaHistoryWikiId=" + viennaHistoryWikiId + ",\n" +
                "  gnd=" + gnd + ",\n" +
                "  wikidataId=" + wikidataId + ",\n" +
                "  birthDate=" + birthDate + ",\n" +
                "  birthPlace=" + birthPlace + ",\n" +
                "  deathDate=" + deathDate + ",\n" +
                "  deathPlace=" + deathPlace + ",\n" +
                "  jobs=" + jobs + ",\n" +
                "  politicalLinkage=" + politicalLinkage + ",\n" +
                "  event=" + event + ",\n" +
                "  estate=" + estate + ",\n" +
                "  seeAlso=" + seeAlso + ",\n" +
                "  resource=" + resource + ",\n" +
                "  links=" + links + "\n" +
                '}';
    }
}
