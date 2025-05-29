package group_05.ase.neo4j_data_access.Service.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.neo4j_data_access.Entity.Tour.TourObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Service.Interface.ITourService;
import org.springframework.data.neo4j.types.GeographicPoint2d;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class TourService implements ITourService {

    private HistoricBuildingService historicBuildingService;
    private HistoricPersonService historicPersonService;
    private HistoricEventService historicEventService;
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public TourObject createTour(GeographicPoint2d start, GeographicPoint2d end, String name, Optional<Double> maxDistance, Optional<Double> maxDuration) {
        return null;
    }

    @Override
    public TourObject createTourBasedOnLocation(GeographicPoint2d location, String name, Optional<Double> maxDistance, Optional<Double> maxDuration) {
        List<ViennaHistoryWikiBuildingObject> buildingsInRadius = historicBuildingService.findHistoricalBuildingWithinRadius(location.getLatitude(), location.getLongitude(), maxDistance.orElse(3000.0));
        return null;

    }

    @Override
    public TourObject getTourByName(String name) {
        return null;
    }

    @Override
    public void deleteTourByName(String name) {

    }

    private Map<String, Double> getLengthDurationOfTour(TourObject tour){
        List<GeographicPoint2d> stops = new ArrayList<>();
        stops.add(tour.getStart());
        tour.getStops().forEach(stop -> stops.add(new GeographicPoint2d(stop.getStop().getLatitude().orElse(0.0), stop.getStop().getLongitude().orElse(0.0) )));

        return accessOpenRoutingService(stops, "foot-walking");
    }



    private Map<String, Double> accessOpenRoutingService(List<GeographicPoint2d> points, String mode){
        //mode = foot-walking
        String url = "https://api.openrouteservice.org/v2/directions/" + mode + "/json";

        List<List<Double>> coordinates = points.stream().map(point -> List.of(point.getLongitude(), point.getLatitude())).toList();
        System.out.println(coordinates.toString());


        Map<String, Object> payload = new HashMap<>();
        payload.put("coordinates", coordinates);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer 5b3ce3597851110001cf62483d55e81c4b86476d84b75792e086fc0b");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Error: " + response.getBody());
            return null;
        }

        try{
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode summary = root.path("routes").get(0).path("summary");

            Double distance = summary.path("distance").asDouble();
            Double duration = summary.path("duration").asDouble();

            return Map.of("distance", distance, "duration", duration);

        } catch (JsonProcessingException e) {
            System.out.println("Could not parse directions response.");
            return null;
        }

    }
}
