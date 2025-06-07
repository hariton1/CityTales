package group_05.ase.orchestrator.service;

import group_05.ase.orchestrator.dto.UserInterestsDTO;
import group_05.ase.orchestrator.dto.ViennaHistoryWikiBuildingObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Service
//public class FilteredBuildingService {

//
//    /**
//     * Filtert Gebäude nach Interessen und Radius.
//     */
//    public List<ViennaHistoryWikiBuildingObject> filterBuildingsByUserInterestsAndLocation(
//            List<ViennaHistoryWikiBuildingObject> allBuildings,
//            List<UserInterestsDTO> userInterests,
//            double latitude,
//            double longitude,
//            double radiusMeters
//    ) {
//        List<String> interestNames = userInterests.stream()
//                .map(UserInterestsDTO::getInterestNameEn)
//                .toList();
//
//        List<ViennaHistoryWikiBuildingObject> filtered = new ArrayList<>();
//        for (ViennaHistoryWikiBuildingObject building : allBuildings) {
//            if (
//                    matchesAnyInterest(building, interestNames)
//                            && isWithinRadius(building, latitude, longitude, radiusMeters)
//            ) {
//                filtered.add(building);
//            }
//        }
//        return filtered;
//    }
//
//    /**
//     * Checkt, ob das Gebäude einen der Interessen matcht.
//     */
//    private boolean matchesAnyInterest(ViennaHistoryWikiBuildingObject building, List<String> interestNames) {
//        for (String interest : interestNames) {
//            if (
//                    containsIgnoreCase(building.getName(), interest)
//                            || containsIgnoreCase(building.getBuildingType().orElse(""), interest)
//                            || containsIgnoreCase(building.getNamedAfter().orElse(""), interest)
//                            || containsIgnoreCase(building.getOtherName().orElse(""), interest)
//                            || containsIgnoreCase(building.getSeeAlso().orElse(""), interest)
//            ) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean containsIgnoreCase(String text, String search) {
//        return text != null && search != null
//                && text.toLowerCase().contains(search.toLowerCase());
//    }
//
//    /**
//     * Berechnet, ob das Gebäude im Radius um die Startkoordinate liegt.
//     */
//    private boolean isWithinRadius(ViennaHistoryWikiBuildingObject building, double centerLat, double centerLon, double radiusMeters) {
//        Optional<Double> optLat = building.getLatitude();
//        Optional<Double> optLon = building.getLongitude();
//        if (optLat.isEmpty() || optLon.isEmpty()) {
//            return false;
//        }
//        double lat = optLat.get();
//        double lon = optLon.get();
//        double distance = haversine(centerLat, centerLon, lat, lon);
//        return distance <= radiusMeters;
//    }
//
//    /**
//     * Haversine-Formel für Entfernung zwischen 2 Punkten in Metern.
//     */
//    private double haversine(double lat1, double lon1, double lat2, double lon2) {
//        final int R = 6371000; // Erd-Radius in Meter
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLon = Math.toRadians(lon2 - lon1);
//        double a =
//                Math.sin(dLat / 2) * Math.sin(dLat / 2)
//                        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        return R * c;
//    }
//}
@Service
public class FilteredBuildingService {

    public List<ViennaHistoryWikiBuildingObject> filterBuildingsByUserInterests(
            List<ViennaHistoryWikiBuildingObject> buildings,
            List<UserInterestsDTO> interests
    ) {
        if (interests == null || interests.isEmpty()) {
            // Kein Filter: alles zurückgeben
            return buildings;
        }

        List<String> interestNames = interests.stream()
                .map(UserInterestsDTO::getInterestNameEn)
                .map(String::toLowerCase)
                .toList();

        return buildings.stream()
                .filter(b -> matchesAnyInterest(b, interestNames))
                .toList();
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
