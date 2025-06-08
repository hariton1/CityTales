package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.DTO.FunFactCardDTO;
import group_05.ase.neo4j_data_access.DTO.FunFactResult;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Service.Interface.IHistoricBuildingService;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactExtractorService;
import group_05.ase.neo4j_data_access.Service.Implementation.FunFactMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funfact")
public class FunFactController {

    private final IHistoricBuildingService historicPlaceService;
    private final FunFactExtractorService funFactExtractorService;

    public FunFactController(
            IHistoricBuildingService historicPlaceService,
            FunFactExtractorService funFactExtractorService
    ) {
        this.historicPlaceService = historicPlaceService;
        this.funFactExtractorService = funFactExtractorService;
    }

    @GetMapping("/by/id/{viennaHistoryWikiId}")
    public ResponseEntity<FunFactCardDTO> getFunFactForBuilding(@PathVariable int viennaHistoryWikiId) {
        ViennaHistoryWikiBuildingObject building = historicPlaceService.getBuildingById(viennaHistoryWikiId);
        if (building == null) return ResponseEntity.notFound().build();

        FunFactResult funFactResult = funFactExtractorService.extractFunFactHybridWithReason(building.getContentGerman());
        FunFactCardDTO dto = FunFactMapper.mapToFunFactCard(building, funFactResult);

        return ResponseEntity.ok(dto);
    }
}
