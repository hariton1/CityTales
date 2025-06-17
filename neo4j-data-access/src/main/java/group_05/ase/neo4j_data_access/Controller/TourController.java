package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.Tour.CreateTourRequestDTO;
import group_05.ase.neo4j_data_access.Entity.Tour.DurationDistanceEstimateDTO;
import group_05.ase.neo4j_data_access.Entity.Tour.TourDTO;
import group_05.ase.neo4j_data_access.Service.Interface.ITourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tour")
public class TourController {

    private final ITourService tourService;

    private static final Logger logger = LoggerFactory.getLogger(TourController.class);

    public TourController(ITourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping(value = "/durationDistanceEstimate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getDurationDistanceEstimate(@RequestBody DurationDistanceEstimateDTO request) {
        logger.info("Fetching duration and distance estimate for request: {}", request.toString());
        Map<String, Double> result = tourService.getDurationDistanceEstimate(
                request.getStart_lat(),
                request.getStart_lng(),
                request.getEnd_lat(),
                request.getEnd_lng(),
                request.getStops()
        );

        if (result == null) {
            logger.info("Fetching duration and distance estimate for request {} return nothing", request.toString());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/createBasedOnInterests")
    public ResponseEntity<List<TourDTO>> createTourBasedOnInterests(@RequestBody CreateTourRequestDTO request) {
        logger.info("Creating tour based on interests for request: {}", request.toString());
        List<TourDTO> tours = tourService.createTours(request);
        return ResponseEntity.ok(tours);
    }
}