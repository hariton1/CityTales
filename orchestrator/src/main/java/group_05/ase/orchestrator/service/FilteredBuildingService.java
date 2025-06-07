package group_05.ase.orchestrator.service;

import group_05.ase.orchestrator.dto.ViennaHistoryWikiBuildingObject;
import group_05.ase.orchestrator.dto.UserInterestsDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class FilteredBuildingService {

    /**
     * Filtert alle Gebäude anhand der User-Interessen.
     * Es reicht ein "contains" in den wichtigsten Feldern (name, buildingType, namedAfter, otherName, seeAlso).
     */
    public List<ViennaHistoryWikiBuildingObject> filterBuildingsByUserInterests(
            List<ViennaHistoryWikiBuildingObject> allBuildings,
            List<UserInterestsDTO> userInterests
    ) {
        // Extrahiere Namen der Interessen und mache alles lowercase zum Vergleichen
        List<String> interestNames = userInterests.stream()
                .map(ui -> ui.getInterestNameEn().toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

        return allBuildings.stream()
                .filter(building -> matchesAnyInterest(building, interestNames))
                .collect(Collectors.toList());
    }

    /**
     * Prüft, ob das Gebäude mit mindestens einem Interesse matcht.
     */
    private boolean matchesAnyInterest(ViennaHistoryWikiBuildingObject building, List<String> interestNames) {
        for (String interest : interestNames) {
            if (
                    containsIgnoreCase(building.getName(), interest) ||
                            containsIgnoreCase(building.getBuildingType().orElse(""), interest) ||
                            containsIgnoreCase(building.getNamedAfter().orElse(""), interest) ||
                            containsIgnoreCase(building.getOtherName().orElse(""), interest) ||
                            containsIgnoreCase(building.getSeeAlso().orElse(""), interest)
            ) {
                return true;
            }
        }
        return false;
    }

    private boolean containsIgnoreCase(String text, String search) {
        if (text == null) return false;
        return text.toLowerCase(Locale.ROOT).contains(search);
    }
}
