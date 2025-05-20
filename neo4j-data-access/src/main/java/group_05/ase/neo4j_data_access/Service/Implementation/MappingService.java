package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Service.Interface.IMappingService;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MappingService implements IMappingService {

    @Override
    public ViennaHistoryWikiPersonObject mapNodeToPersonEntity(Node node) {
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


    public ViennaHistoryWikiEventObject mapNodeToEventEntity(Node node) {
        ViennaHistoryWikiEventObject eventEntity = new ViennaHistoryWikiEventObject();

        eventEntity.setViennaHistoryWikiId(node.get("viennaHistoryWikiId").asInt());
        eventEntity.setName(node.get("name").asString());
        eventEntity.setUrl(node.get("url").asString());
        eventEntity.setDateFrom(Optional.ofNullable(getSafeString(node, "dateFrom")));
        eventEntity.setDateTo(Optional.ofNullable(getSafeString(node, "dateTo")));
        eventEntity.setOrganizer(Optional.ofNullable(getSafeString(node, "organizer")));
        eventEntity.setParticipantCount(Optional.ofNullable(getSafeString(node, "participantCount")));
        eventEntity.setGnd(Optional.ofNullable(getSafeString(node, "gnd")));
        eventEntity.setWikidataId(Optional.ofNullable(getSafeString(node, "wikidataId")));
        eventEntity.setResource(Optional.ofNullable(getSafeString(node, "resource")));
        eventEntity.setTypeOfEvent(Optional.ofNullable(getSafeString(node, "typeOfEvent")));
        eventEntity.setTopic(Optional.ofNullable(getSafeString(node, "topic")));
        eventEntity.setSeeAlso(Optional.ofNullable(getSafeString(node, "seeAlso")));
        if (node.containsKey("links")) {
            eventEntity.setLinks(node.get("links").asList(Value::asString));
        } else {
            eventEntity.setLinks(new ArrayList<>());
        }

        if (node.containsKey("imageUrls")) {
            eventEntity.setImageUrls(node.get("imageUrls").asList(Value::asString));
        } else {
            eventEntity.setImageUrls(new ArrayList<>());
        }

        return eventEntity;
    }


    public ViennaHistoryWikiBuildingObject mapNodeToHistoricalBuildingEntity(Node node) {
        ViennaHistoryWikiBuildingObject building = new ViennaHistoryWikiBuildingObject();

        building.setViennaHistoryWikiId(node.get("viennaHistoryWikiId").asInt());
        building.setName(node.get("name").asString());
        building.setUrl(node.get("url").asString());
        building.setBuildingType(Optional.ofNullable( getSafeString(node, "buildingType")));
        building.setDateFrom(Optional.ofNullable( getSafeString(node, "dateFrom")));
        building.setDateTo(Optional.ofNullable( getSafeString(node, "dateTo")));
        building.setOtherName(Optional.ofNullable( getSafeString(node, "otherName")));
        building.setPreviousName(Optional.ofNullable( getSafeString(node, "previousName")));
        building.setNamedAfter(Optional.ofNullable( getSafeString(node, "namedAfter")));
        building.setEntryNumber(Optional.ofNullable( getSafeString(node, "entryNumber")));
        building.setArchitect(Optional.ofNullable( getSafeString(node, "architect")));
        building.setFamousResidents(Optional.ofNullable( getSafeString(node, "famousResidents")));
        building.setGnd(Optional.ofNullable( getSafeString(node, "gnd")));
        building.setWikidataId(Optional.ofNullable( getSafeString(node, "wikidataId")));
        building.setSeeAlso(Optional.ofNullable( getSafeString(node, "seeAlso")));
        building.setResource(Optional.ofNullable( getSafeString(node, "resource")));

        if (node.containsKey("latitude") && node.containsKey("longitude")) {
            double latitude = node.get("latitude").asDouble();
            double longitude = node.get("longitude").asDouble();
            building.setLatitude(Optional.of(latitude));
            building.setLongitude(Optional.of(longitude));
        } else {
            building.setLatitude(Optional.empty());
            building.setLongitude(Optional.empty());
        }

        if (node.containsKey("links")) {
            building.setLinks(node.get("links").asList(Value::asString));
        } else {
            building.setLinks(new ArrayList<>());
        }

        if (node.containsKey("imageUrls")) {
            building.setImageUrls(node.get("imageUrls").asList(Value::asString));
        } else {
            building.setImageUrls(new ArrayList<>());
        }

        return building;
    }





    private String getSafeString(Node node, String key) {
        return node.containsKey(key) ? node.get(key).asString() : "N/A";
    }


}
