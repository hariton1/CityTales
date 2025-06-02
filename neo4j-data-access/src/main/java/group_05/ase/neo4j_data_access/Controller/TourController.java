package group_05.ase.neo4j_data_access.Controller;

import group_05.ase.neo4j_data_access.Entity.Tour.CreateTourRequestDTO;
import group_05.ase.neo4j_data_access.Entity.Tour.DurationDistanceEstimateDTO;
import group_05.ase.neo4j_data_access.Entity.Tour.TourObject;
import group_05.ase.neo4j_data_access.Service.Interface.ITourService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tour")
public class TourController {

    private final ITourService tourService;

    public TourController(ITourService tourService) {
        this.tourService = tourService;
        System.out.println("TourController created");
    }

    @PostMapping(value = "/durationDistanceEstimate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getDurationDistanceEstimate(@RequestBody DurationDistanceEstimateDTO request) {

        System.out.println("Received request: " + request.toString());

        Map<String, Double> result = tourService.getDurationDistanceEstimate(
                request.getStart_lat(),
                request.getStart_lng(),
                request.getEnd_lat(),
                request.getEnd_lng(),
                request.getStops()
        );

        if (result == null) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(result);
    }

    @PostMapping("/createBasedOnInterests")
    public ResponseEntity<List<TourObject>> createTourBasedOnInterests(@RequestBody CreateTourRequestDTO request) {
        System.out.println("Received request: " + request.toString());
        List<TourObject> tours = tourService.createTours(request);
        return ResponseEntity.ok(tours);
    }
}