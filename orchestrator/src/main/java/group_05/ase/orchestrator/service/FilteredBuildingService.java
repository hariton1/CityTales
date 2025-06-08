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
        return sorted;
    }

    private boolean matchesAnyInterest(ViennaHistoryWikiBuildingObject building, List<String> interestNames) {
        for (String interest : interestNames) {
            Set<String> allSynonyms = INTEREST_SYNONYMS.getOrDefault(interest, Set.of(interest));
            for (String synonym : allSynonyms) {
                if (
                        containsIgnoreCase(building.getName(), synonym) ||
                                containsIgnoreCase(building.getBuildingType().orElse(""), synonym) ||
                                containsIgnoreCase(building.getNamedAfter().orElse(""), synonym) ||
                                containsIgnoreCase(building.getOtherName().orElse(""), synonym) ||
                                containsIgnoreCase(building.getSeeAlso().orElse(""), synonym) //||
//                                containsIgnoreCase(building.getContentEnglish(), synonym) ||
//                                containsIgnoreCase(building.getContentGerman(), synonym)
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsIgnoreCase(String haystack, String needle) {
        return haystack != null && haystack.toLowerCase().contains(needle);
    }

    private static final Map<String, Set<String>> INTEREST_SYNONYMS = Map.ofEntries(
            Map.entry("music", Set.of(
                    "music", "musician", "song", "songs", "concert", "concerts", "opera", "band", "choir", "orchestra", "composer", "instrument", "instruments",
                    "musik", "musiker", "lied", "lieder", "konzert", "konzerte", "oper", "chor", "orchester", "komponist", "instrumente"
            )),
            Map.entry("literature", Set.of(
                    "literature", "book", "books", "novel", "novels", "writer", "author", "authors", "library", "story", "stories", "poetry", "poet", "reading",
                    "literatur", "buch", "bücher", "roman", "romane", "schriftsteller", "autor", "autoren", "bibliothek", "geschichte", "geschichten", "dichtung", "dichter", "lesen"
            )),
            Map.entry("buildings", Set.of(
                    "building", "buildings", "architecture", "structure", "structures", "edifice", "palace", "hall", "halls", "tower", "castle", "monument", "landmark",
                    "gebäude", "architektur", "struktur", "strukturen", "bauwerk", "palast", "halle", "hallen", "turm", "schloss", "denkmal", "wahrzeichen"
            )),
            Map.entry("medic", Set.of(
                    "medic", "medical", "medicine", "hospital", "clinic", "pharmacy", "doctor", "nurse", "health", "care", "treatment", "cure",
                    "medizin", "medizinisch", "krankenhaus", "klinik", "apotheke", "arzt", "ärztin", "pflege", "gesundheit", "behandlung", "heilung", "patient"
            )),
            Map.entry("movies", Set.of(
                    "movie", "movies", "film", "cinema", "actor", "actress", "director", "screen", "festival",
                    "filme", "kino", "theater", "schauspieler", "schauspielerin", "regisseur", "leinwand"
            )),
            Map.entry("paintings", Set.of(
                    "painting", "paintings", "art", "artist", "gallery", "museum", "exhibition", "portrait", "canvas",
                    "gemälde", "malerei", "kunst", "künstler", "künstlerin", "galerie", "ausstellung", "porträt", "leinwand"
            )),
            Map.entry("banking", Set.of(
                    "bank", "banking", "finance", "financial", "account", "accounts", "money", "currency", "institution", "loan", "branch",
                    "bankwesen", "finanz", "finanzen", "konto", "konten", "geld", "währung", "institut", "kredit", "filiale"
            )),
            Map.entry("newspapers", Set.of(
                    "newspaper", "newspapers", "news", "journal", "press", "article", "articles", "headline", "editor", "publisher",
                    "zeitung", "zeitungen", "nachrichten", "presse", "artikel", "schlagzeile", "redakteur", "verleger"
            )),
            Map.entry("maps", Set.of(
                    "map", "maps", "atlas", "plan", "district", "city", "layout", "street", "streets",
                    "karte", "karten", "stadtplan", "bezirk", "stadt", "lageplan", "straße", "straßen"
            )),
            Map.entry("relations", Set.of(
                    "relation", "relations", "relationship", "relationships", "connection", "connections", "network", "association", "link", "links",
                    "beziehung", "beziehungen", "verbindung", "verbindungen", "netzwerk", "assoziation"
            )),
            Map.entry("baths", Set.of(
                    "bath", "baths", "bathhouse", "spa", "thermal", "hot spring", "pool", "wellness",
                    "badehaus", "bad", "bäder", "thermalbad", "therme", "quelle"
            )),
            Map.entry("hills", Set.of(
                    "hill", "hills", "mount", "mountain", "elevation", "summit", "peak", "ridge",
                    "hügel", "berg", "berge", "anhöhe", "gipfel", "kamm"
            )),
            Map.entry("education", Set.of(
                    "education", "school", "schools", "university", "universities", "college", "academy", "institution", "learning", "class", "classes",
                    "ausbildung", "schule", "schulen", "universität", "hochschule", "akademie", "institut", "lernen", "klasse", "klassen"
            )),
            Map.entry("breweries", Set.of(
                    "brewery", "breweries", "beer", "beers", "brewing", "taproom", "pub", "brew", "lager",
                    "brauerei", "brauereien", "bier", "biere", "brauen", "schankraum", "lokal", "ale"
            )),
            Map.entry("bridges", Set.of(
                    "bridge", "bridges", "viaduct", "span", "footbridge", "overpass",
                    "brücke", "brücken", "viadukt", "überführung", "steg"
            )),
            Map.entry("fountains", Set.of(
                    "fountain", "fountains", "well", "spring", "water feature", "monument", "sculpture",
                    "brunnen", "quelle", "wasserspiel", "denkmal", "skulptur"
            )),
            Map.entry("statues", Set.of(
                    "statue", "statues", "sculpture", "sculptures", "monument", "memorial", "bust", "figure",
                    "statuen", "skulptur", "skulpturen", "büste", "figur"
            )),
            Map.entry("monuments", Set.of(
                    "monument", "monuments", "memorial", "statue", "sculpture", "plaque", "obelisk",
                    "denkmal", "denkmäler", "tafel"
            )),
            Map.entry("cemeteries", Set.of(
                    "cemetery", "cemeteries", "graveyard", "grave", "tomb", "burial", "mausoleum",
                    "friedhof", "friedhöfe", "grab", "grabstätte", "begräbnis"
            )),
            Map.entry("parks", Set.of(
                    "park", "parks", "garden", "gardens", "green", "playground", "area", "open space", "meadow",
                    "gärten", "garten", "grünanlage", "spielplatz", "fläche", "wiese"
            )),
            Map.entry("religion", Set.of(
                    "religion", "religious", "church", "churches", "chapel", "synagogue", "mosque", "temple", "parish", "congregation", "faith", "belief",
                    "religiös", "kirche", "kirchen", "kapelle", "synagoge", "moschee", "tempel", "pfarre", "gemeinde", "glaube"
            ))
    );



}
