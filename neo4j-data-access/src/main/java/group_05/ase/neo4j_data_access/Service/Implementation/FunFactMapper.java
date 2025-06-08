package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.DTO.FunFactCardDTO;
import group_05.ase.neo4j_data_access.DTO.FunFactResult;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;

import java.util.List;

public class FunFactMapper {

    public static FunFactCardDTO mapToFunFactCard(ViennaHistoryWikiBuildingObject building, FunFactResult funFactResult) {
        String headline = building.getName() != null ? building.getName() : "Wusstest du schon?";

        String imageUrl = "default.jpg";
        List<String> imageUrls = building.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            imageUrl = imageUrls.get(0); // Erstes Bild nehmen
        }

        return new FunFactCardDTO(
                headline,
                funFactResult.getSentence(),
                imageUrl,
                funFactResult.getScore(),
                funFactResult.getReason()
        );
    }
}

