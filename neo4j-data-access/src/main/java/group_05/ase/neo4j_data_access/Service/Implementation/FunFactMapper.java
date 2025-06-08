package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.DTO.FunFactCardDTO;
import group_05.ase.neo4j_data_access.DTO.FunFactResult;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiPersonObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiEventObject;

import java.util.List;

public class FunFactMapper {

    public static FunFactCardDTO mapToFunFactCard(ViennaHistoryWikiBuildingObject building, FunFactResult funFactResult) {
        String headline = building.getName() != null ? building.getName() : "Wusstest du schon?";
        String imageUrl = pickFirstImage(building.getImageUrls());
        return new FunFactCardDTO(
                headline,
                funFactResult.getSentence(),
                imageUrl,
                funFactResult.getScore(),
                funFactResult.getReason()
        );
    }

    public static FunFactCardDTO mapToFunFactCard(ViennaHistoryWikiPersonObject person, FunFactResult funFactResult) {
        String headline = person.getName() != null ? person.getName() : "Berühmte Person";
        String imageUrl = pickFirstImage(person.getImageUrls());
        return new FunFactCardDTO(
                headline,
                funFactResult.getSentence(),
                imageUrl,
                funFactResult.getScore(),
                funFactResult.getReason()
        );
    }

    public static FunFactCardDTO mapToFunFactCard(ViennaHistoryWikiEventObject event, FunFactResult funFactResult) {
        // Angenommen Events haben ein Titel-Feld, sonst passe an!
        String headline = event.getName() != null ? event.getName() : "Historisches Ereignis";
        String imageUrl = pickFirstImage(event.getImageUrls());
        return new FunFactCardDTO(
                headline,
                funFactResult.getSentence(),
                imageUrl,
                funFactResult.getScore(),
                funFactResult.getReason()
        );
    }

    private static String pickFirstImage(List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        } else {
            return "default.jpg";
        }
    }
}
