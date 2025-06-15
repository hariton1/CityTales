package group_05.ase.orchestrator.service;

import group_05.ase.orchestrator.dto.ArticleWeightDTO;
import group_05.ase.orchestrator.dto.MatchRequest;
import group_05.ase.orchestrator.dto.UserInterestsDTO;
import group_05.ase.orchestrator.dto.ViennaHistoryWikiBuildingObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilteredBuildingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String QDRANT_URL = "http://localhost:8081/";

    public List<ViennaHistoryWikiBuildingObject> filterBuildingsByUserInterests(
            List<ViennaHistoryWikiBuildingObject> buildings,
            List<UserInterestsDTO> interests,
            List<ArticleWeightDTO> articleWeights
    ) {
        System.out.println("Input buildings: " + (buildings == null ? "null" : buildings.size()));
        System.out.println("Input interests: " + (interests == null ? "null" : interests.size()));
        System.out.println("Input articleWeights: " + (articleWeights == null ? "null" : articleWeights.size()));

        Map<Integer, Float> weightMap = articleWeights.stream()
                .collect(Collectors.toMap(
                        ArticleWeightDTO::getArticleId,
                        ArticleWeightDTO::getWeight
                ));

        List<String> interestNames = interests == null ? List.of() :
                interests.stream()
                        .map(UserInterestsDTO::getInterestNameDe)
                        .map(String::toLowerCase)
                        .toList();

        List<Integer> interestFilteredBuildings = (interests == null || interests.isEmpty())
                ? null
                : this.getEntitiesFromQdrant(interestNames, "WienGeschichteWikiBuildings");

        List<ViennaHistoryWikiBuildingObject> filtered = buildings.stream()
                .filter(b -> {
                    if (interests == null || interests.isEmpty() || interestFilteredBuildings == null) {
                        return true;
                    }
                    return interestFilteredBuildings.contains(b.getViennaHistoryWikiId());
                })
                .toList();

        // Make a mutable copy and sort
        List<ViennaHistoryWikiBuildingObject> sorted = new ArrayList<>(filtered);
        sorted.sort((a, b) -> {
            float wA = weightMap.getOrDefault(a.getViennaHistoryWikiId(), 0.0f);
            float wB = weightMap.getOrDefault(b.getViennaHistoryWikiId(), 0.0f);
            return Float.compare(wB, wA);
        });
        System.out.println("Filtered buildings: " + filtered.size());
        return sorted;
    }

    private List<Integer> getEntitiesFromQdrant(List<String> interestsNames, String collectionName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        MatchRequest dto = new MatchRequest();
        dto.setCollectionName(collectionName);
        dto.setInterests(interestsNames);
        dto.setResultSize(100);

        System.out.println(dto);

        HttpEntity<MatchRequest> entity = new HttpEntity<>(dto, headers);
        ResponseEntity<List> response = restTemplate.exchange(QDRANT_URL + "categorize/match", HttpMethod.POST, entity, List.class);
        return response.getBody();
    }
}
