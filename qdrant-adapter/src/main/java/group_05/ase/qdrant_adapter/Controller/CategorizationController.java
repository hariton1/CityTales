package group_05.ase.qdrant_adapter.Controller;

import group_05.ase.qdrant_adapter.Entity.MatchRequest;
import group_05.ase.qdrant_adapter.Entity.UpsertEntryRequest;
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
    private final IEmbeddingsService embeddingsService;

    @Autowired
    public CategorizationController(IVectorDBService vectorDBService, IEmbeddingsService embeddingsService) {
        this.vectorDBService = vectorDBService;
        this.embeddingsService = embeddingsService;
    }

    @PostMapping("/init")
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

    @GetMapping("/match")
    @ResponseBody
    public ResponseEntity<List<String>> matching(@RequestBody MatchRequest dto) {

        List<Float> interestsVector = embeddingsService.getInterestsEmbedding(dto.getInterests());
        System.out.println("interestVector:" + interestsVector.size());
        return ResponseEntity.ok(vectorDBService.doMatching(interestsVector, dto.getCollectionName(), dto.getResultSize()));
    }
}
