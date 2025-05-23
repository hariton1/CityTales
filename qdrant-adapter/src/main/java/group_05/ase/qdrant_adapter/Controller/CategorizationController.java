package group_05.ase.qdrant_adapter.Controller;

import group_05.ase.qdrant_adapter.Entity.MatchRequest;
import group_05.ase.qdrant_adapter.Entity.UpsertEntryRequest;
import group_05.ase.qdrant_adapter.Service.Implementation.OpenAiService;
import group_05.ase.qdrant_adapter.Service.Interface.IEmbeddingsService;
import group_05.ase.qdrant_adapter.Service.Interface.IVectorDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categorize")
public class CategorizationController {

    private final IVectorDBService vectorDBService;
    private final OpenAiService openAiService;

    @Autowired
    public CategorizationController(IVectorDBService vectorDBService, OpenAiService openAiService ) {
        this.vectorDBService = vectorDBService;
        this.openAiService = openAiService;
    }

/*    @PostMapping("/init")
    @ResponseBody
    public ResponseEntity<String> initializeCollections() {
        vectorDBService.createCollection("historicPersons");
        vectorDBService.createCollection("historicPlaces");

        return ResponseEntity.ok("Collections initialized.");
    }

    @PostMapping("/teardown")
    @ResponseBody
    public ResponseEntity<String> teardownCollections() {
        vectorDBService.deleteCollection("historicPersons");
        vectorDBService.deleteCollection("historicPlaces");

        return ResponseEntity.ok("Collections deleted.");
    }

    @PostMapping("/upsert")
    @ResponseBody
    public ResponseEntity<String> upsertEntry(@RequestBody UpsertEntryRequest dto) {

        List<Float> articleEmbedding = embeddingsService.getArticleEmbedding(dto.getContent());
        vectorDBService.upsertEntry(articleEmbedding, dto.getCollectionName(), dto.getWikiDataId());
        return ResponseEntity.ok("Entry upserted.");
    }
    */


    @GetMapping("/match")
    @ResponseBody
    public ResponseEntity<List<Integer>> matching(@RequestBody MatchRequest dto) {

        String combinedInterests = String.join(", ", dto.getInterests());

        float[] embedding = openAiService.getEmbedding(combinedInterests);
        System.out.println("interestVector:" + embedding.length);
        return ResponseEntity.ok(vectorDBService.doMatching(embedding, dto.getCollectionName(), dto.getResultSize()));
    }
}
