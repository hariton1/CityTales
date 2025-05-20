package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Config.Neo4jProperties;
import group_05.ase.neo4j_data_access.DTO.HistoricPersonDTO;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricPersonService;
import group_05.ase.neo4j_data_access.Service.Interface.IWikipediaExtractorService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricPersonService implements IHistoricPersonService {
    private final String NEO4J_URL;
    private final String NEO4J_USER;
    private final String NEO4J_PASSWORD;

    private Driver driver;
    private final IWikipediaExtractorService wikipediaExtractorService;

    public HistoricPersonService(IWikipediaExtractorService wikipediaExtractorService, Neo4jProperties properties) {
        this.wikipediaExtractorService = wikipediaExtractorService;

        this.NEO4J_URL = properties.getUrl();
        this.NEO4J_USER = properties.getUser();
        this.NEO4J_PASSWORD = properties.getPassword();
    }

    @PostConstruct
    public void init() {
        driver = GraphDatabase.driver(
                NEO4J_URL,
                AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD)
        );
        System.out.println("Neo4j driver initialized.");
    }

    @PreDestroy
    public void close() {
        if (driver != null) {
            driver.close();
            System.out.println("Neo4j driver closed.");
        }
    }

    public HistoricPersonDTO getPersonById(int viennaHistoryWikiId) {
        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiPersons {viennaHistoryWikiId: $viennaHistoryWikiId}) RETURN p";
            Record record = session.executeRead(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).single());

            Node node = record.get("p").asNode();
            ViennaHistoryWikiPersonObject entity = mapNodeToPersonEntity(node);

            String content = extractMainArticleText(entity.getUrl());
            return new HistoricPersonDTO(entity,content);

        } catch (NoSuchRecordException e) {
            System.err.println("WienGeschichteWikiPersons with ID " + viennaHistoryWikiId + " not found.");
            return null;
        }
    }

    public List<HistoricPersonDTO> getPersonsByPartialName(String partialName) {
        List<HistoricPersonDTO> personDTOs = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiPersons) " +
                    "WHERE toLower(p.name) CONTAINS toLower($name) " +
                    "RETURN p";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("name", partialName)).list()
            );

            for (Record record : records) {
                Node node = record.get("p").asNode();
                ViennaHistoryWikiPersonObject entity = mapNodeToPersonEntity(node);

                String content = extractMainArticleText(entity.getUrl());
                HistoricPersonDTO dto = new HistoricPersonDTO(entity, content);
                personDTOs.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving people by name: " + e.getMessage());
        }

        return personDTOs;
    }

    public List<HistoricPersonDTO> getAllLinkedHistoricPersonsById(int viennaHistoryWikiId) {
        List<HistoricPersonDTO> linkedPersons = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = "MATCH (p:WienGeschichteWikiPersons {viennaHistoryWikiId: $viennaHistoryWikiId})-[:HAS_LINK_TO]->(linked:WienGeschichteWikiPersons) " +
                    "RETURN linked";

            List<Record> records = session.readTransaction(tx ->
                    tx.run(query, Values.parameters("viennaHistoryWikiId", viennaHistoryWikiId)).list()
            );

            for (Record record : records) {
                Node linkedNode = record.get("linked").asNode();
                ViennaHistoryWikiPersonObject entity = mapNodeToPersonEntity(linkedNode);

                String content = extractMainArticleText(entity.getUrl());
                HistoricPersonDTO dto = new HistoricPersonDTO(entity, content);
                linkedPersons.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving linked historic persons for wikiDataId " + viennaHistoryWikiId + ": " + e.getMessage());
        }

        return linkedPersons;
    }

    private ViennaHistoryWikiPersonObject mapNodeToPersonEntity(Node node) {
        ViennaHistoryWikiPersonObject personEntity = new ViennaHistoryWikiPersonObject();

        personEntity.setViennaHistoryWikiId(node.get("viennaHistoryWikiId").asInt());
        personEntity.setName(node.get("name").asString());
        personEntity.setUrl(node.get("url").asString());
        personEntity.setPersonName(Optional.ofNullable(getSafeString(node, "personName")));
        personEntity.setAlternativeName(Optional.ofNullable(getSafeString(node, "alternativeName")));
        personEntity.setTitles(Optional.ofNullable(getSafeString(node, "titles")));
        personEntity.setSex(Optional.ofNullable(getSafeString(node, "sex")));
        personEntity.setGnd(Optional.ofNullable(getSafeString(node, "gnd")));
        personEntity.setWikidataId(Optional.ofNullable(getSafeString(node, "wikidataId")));
        personEntity.setBirthDate(Optional.ofNullable(getSafeString(node, "birthDate")));
        personEntity.setBirthPlace(Optional.ofNullable(getSafeString(node, "birthPlace")));
        personEntity.setDeathDate(Optional.ofNullable(getSafeString(node, "deathDate")));
        personEntity.setDeathPlace(Optional.ofNullable(getSafeString(node, "deathPlace")));
        personEntity.setJobs(Optional.ofNullable(getSafeString(node, "jobs")));
        personEntity.setPoliticalLinkage(Optional.ofNullable(getSafeString(node, "politicalLinkage")));
        personEntity.setEvent(Optional.ofNullable(getSafeString(node, "event")));
        personEntity.setEstate(Optional.ofNullable(getSafeString(node, "estate")));
        personEntity.setSeeAlso(Optional.ofNullable(getSafeString(node, "seeAlso")));
        personEntity.setResource(Optional.ofNullable(getSafeString(node, "resource")));

        if (node.containsKey("links")) {
            personEntity.setLinks(node.get("links").asList(Value::asString));
        } else {
            personEntity.setLinks(new ArrayList<>());
        }

        if (node.containsKey("imageUrls")) {
            personEntity.setImageUrls(node.get("imageUrls").asList(Value::asString));
        } else {
            personEntity.setImageUrls(new ArrayList<>());
        }

        return personEntity;
    }

    private String getSafeString(Node node, String key) {
        return node.containsKey(key) ? node.get(key).asString() : "N/A";
    }

    private String extractMainArticleText(String url) {
        StringBuilder textContent = new StringBuilder();

        try {
            Document doc = Jsoup.connect(url).get();

            Element contentDiv = doc.selectFirst("div.mw-parser-output");

            if (contentDiv != null) {
                Elements paragraphs = contentDiv.select("p");

                for (Element paragraph : paragraphs) {
                    String text = paragraph.text().trim();
                    if (!text.isEmpty()) {
                        textContent.append(text).append("\n\n");
                    }
                }
            } else {
                System.err.println("Main content div not found.");
            }

        } catch (IOException e) {
            System.err.println("Error fetching URL: " + e.getMessage());
        }

        String[] parts = textContent.toString().split("\n", 7);
        if (parts.length < 7) {
            return "";
        } else {
            String rest = parts[6];
            rest = rest.replaceAll("\n", "");
            return rest;
        }
    }
}
