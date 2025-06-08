package group_05.ase.orchestrator.service;

import group_05.ase.orchestrator.dto.ArticleWeightDTO;
import group_05.ase.orchestrator.dto.UserInterestsDTO;
import group_05.ase.orchestrator.dto.ViennaHistoryWikiBuildingObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilteredBuildingService {



    public List<ViennaHistoryWikiBuildingObject> filterBuildingsByUserInterests(
            List<ViennaHistoryWikiBuildingObject> buildings,
            List<UserInterestsDTO> interests,
            List<ArticleWeightDTO> articleWeights // <-- pass in here!
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
                        .map(UserInterestsDTO::getInterestNameEn)
                        .map(String::toLowerCase)
                        .toList();

        List<ViennaHistoryWikiBuildingObject> filtered = buildings.stream()
                .filter(b -> interests == null || interests.isEmpty() || matchesAnyInterest(b, interestNames))
                .toList();

        // Make a mutable copy and sort
        List<ViennaHistoryWikiBuildingObject> sorted = new ArrayList<>(filtered);
        sorted.sort((a, b) -> {
            float wA = weightMap.getOrDefault(a.getViennaHistoryWikiId(), 0.0f);
            float wB = weightMap.getOrDefault(b.getViennaHistoryWikiId(), 0.0f);
            return Float.compare(wB, wA);
        });
        System.out.println("Filtered buildings: " + filtered.size());
        System.out.println("Sorted buildings: " + sorted.size());
        return sorted;
    }

    private boolean matchesAnyInterest(ViennaHistoryWikiBuildingObject building, List<String> interestNames) {
        for (String interest : interestNames) {
            if (
                    containsIgnoreCase(building.getName(), interest)
                            || containsIgnoreCase(building.getBuildingType().orElse(""), interest)
                            || containsIgnoreCase(building.getNamedAfter().orElse(""), interest)
                            || containsIgnoreCase(building.getOtherName().orElse(""), interest)
                            || containsIgnoreCase(building.getSeeAlso().orElse(""), interest)
            ) {
                return true;
            }
        }
        return false;
    }

    private boolean containsIgnoreCase(String haystack, String needle) {
        return haystack != null && haystack.toLowerCase().contains(needle);
    }
}
