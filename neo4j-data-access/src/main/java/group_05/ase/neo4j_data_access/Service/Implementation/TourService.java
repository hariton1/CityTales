package group_05.ase.neo4j_data_access.Service.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import group_05.ase.neo4j_data_access.Entity.Tour.CreateTourRequestDTO;
import group_05.ase.neo4j_data_access.Entity.Tour.MatchRequest;
import group_05.ase.neo4j_data_access.Entity.Tour.TourObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Service.Interface.ITourService;
import org.neo4j.cypherdsl.core.Match;
import org.springframework.data.neo4j.types.GeographicPoint2d;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.*;

@Service
public class TourService implements ITourService {

    private HistoricBuildingService historicBuildingService;
    private HistoricPersonService historicPersonService;
    private HistoricEventService historicEventService;

    public TourService(HistoricBuildingService historicBuildingService, HistoricPersonService historicPersonService, HistoricEventService historicEventService) {
        this.historicBuildingService = historicBuildingService;
        this.historicPersonService = historicPersonService;
        this.historicEventService = historicEventService;
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final String USERDB_URL = "http://localhost:8090/";
    private final String QDRANT_URL = "http://localhost:8081/";
    private final Integer MAX_ROUTES = 10;


    @Override
    public List<TourObject> createTours(CreateTourRequestDTO dto) {

        //HTTP Request to User Interst DB
        List<String> interest_ids = getInterestsFromDB(dto.getUserId());
        System.out.println(interest_ids);

        //HTTP Request to QDRANT for related articles

        List<Integer> buildings_wikidata_ids = getEntititesFromQdrant(interest_ids, "WienGeschichteWikiBuildings");
        List<Integer> events_wikidata_ids = getEntititesFromQdrant(interest_ids, "WienGeschichteWikiEvents");
        List<Integer> persons_wikidata_ids = getEntititesFromQdrant(interest_ids, "WienGeschichteWikiPersons");

        Set<Integer> related_building_wikidata_ids = new HashSet<>();
        for(Integer event_id: events_wikidata_ids){
            this.historicEventService.getAllLinkedHistoricBuildingsById(event_id).forEach(event -> related_building_wikidata_ids.add(event.getViennaHistoryWikiId()));
        }

        for(Integer event_id: persons_wikidata_ids){
            this.historicPersonService.getAllLinkedHistoricBuildingsById(event_id).forEach(person -> related_building_wikidata_ids.add(person.getViennaHistoryWikiId()));
        }


        System.out.println("Direct buildungs size: " + buildings_wikidata_ids.size());
        System.out.println("Realted buildings size: " + related_building_wikidata_ids.size());

        buildings_wikidata_ids.addAll(related_building_wikidata_ids);
        buildings_wikidata_ids = buildings_wikidata_ids.stream().distinct().toList();

        List<GeographicPoint2d> stops = new ArrayList<>();
        stops.add(new GeographicPoint2d(dto.getStart_lat(), dto.getStart_lng()));

        Map<GeographicPoint2d, Integer> building_dict = new HashMap<>();

        for(Integer building_id : buildings_wikidata_ids) {
            ViennaHistoryWikiBuildingObject building = historicBuildingService.getBuildingById(building_id);
            System.out.println(building);
            if(building.getLatitude().isEmpty() || building.getLongitude().isEmpty() ) {
                continue;
            }
            GeographicPoint2d stop = new GeographicPoint2d(building.getLatitude().get(), building.getLongitude().get());
            building_dict.put(stop, building_id);
            System.out.println(stop);
            stops.add(stop);
        }

        if(dto.getPredefined_stops() != null && dto.getPredefined_stops().size() > 0) {
            dto.getPredefined_stops().forEach(stop -> stops.add(new GeographicPoint2d(stop.getLatitude().get(), stop.getLongitude().get())));

        }
        stops.add(new GeographicPoint2d(dto.getEnd_lat(), dto.getEnd_lng()));

        List<List<Float>> distanceMatrix = getMetricMatrix(stops, "distance");
        System.out.println("Distance Matrix: " + distanceMatrix);

        List<List<GeographicPoint2d>> routes = findRoutesBFS(stops, distanceMatrix, dto.getMinDistance(), dto.getMaxDistance(), dto.getMinIntermediateStops(), MAX_ROUTES);
        List<TourObject> tours = routes.stream().map(route -> buildTourObject(route, dto.getUserId(), building_dict)).toList();

        return tours;
    }

    private TourObject buildTourObject(List<GeographicPoint2d> stops, String userId, Map<GeographicPoint2d, Integer> building_dict) {
        TourObject tourObject = new TourObject();
        tourObject.setStartLng(stops.get(0).getLongitude());
        tourObject.setStartLat(stops.get(0).getLatitude());
        stops.remove(0); //Remove first stop
        tourObject.setEndLat(stops.get(stops.size() - 1).getLatitude());
        tourObject.setEndLng(stops.get(stops.size() -1).getLongitude());
        stops.remove(stops.size() - 1); // remove last stop
        tourObject.setUserId(userId);
        tourObject.setName("Tour from " + Date.from(Instant.now()));
        tourObject.setDescription("Tour created at " + Date.from(Instant.now()));
        List<ViennaHistoryWikiBuildingObject> stops_list = new ArrayList<>();
        for(GeographicPoint2d stop : stops) {
            ViennaHistoryWikiBuildingObject building = historicBuildingService.getBuildingById(building_dict.get(stop));
            stops_list.add(building);
        }
        tourObject.setStops(stops_list);
        Map<String, Double> durations = getLengthDurationOfTour(tourObject);
        tourObject.setDistance(durations.get("distance"));
        tourObject.setDurationEstimate(durations.get("duration"));

        return tourObject;
    }



    public List<List<GeographicPoint2d>> findRoutesBFS(
            List<GeographicPoint2d> stops,
            List<List<Float>> distanceMatrix,
            double minDistance,
            double maxDistance,
            int minIntermediateStops, // new parameter
            int maxRoutes
    ) {
        int N = stops.size();
        int startIdx = 0;
        int endIdx = N - 1;

        List<List<GeographicPoint2d>> validRoutes = new ArrayList<>();

        Queue<RouteState> queue = new LinkedList<>();

        List<Integer> initialPath = new ArrayList<>();
        initialPath.add(startIdx);

        queue.add(new RouteState(initialPath, 0.0));

        while (!queue.isEmpty()) {
            if (validRoutes.size() >= maxRoutes) break;

            RouteState state = queue.poll();
            List<Integer> path = state.path;
            double currentDistance = state.distance;

            int currentStop = path.get(path.size() - 1);

            if (currentStop == endIdx) {
                int intermediateStops = path.size() - 2; // exclude start and end

                if (intermediateStops >= minIntermediateStops &&
                        currentDistance >= minDistance &&
                        currentDistance <= maxDistance) {

                    // Convert path of indices to path of GeographicPoint2d
                    List<GeographicPoint2d> route = new ArrayList<>();
                    for (Integer idx : path) {
                        route.add(stops.get(idx));
                    }
                    validRoutes.add(route);
                }

                continue;
            }

            for (int nextStop = 0; nextStop < N; nextStop++) {
                if (nextStop == startIdx && path.size() > 1) continue;

                if (path.contains(nextStop)) continue;

                double nextDistance = distanceMatrix.get(currentStop).get(nextStop);
                double newTotalDistance = currentDistance + nextDistance;

                if (newTotalDistance > maxDistance) continue;

                List<Integer> newPath = new ArrayList<>(path);
                newPath.add(nextStop);

                queue.add(new RouteState(newPath, newTotalDistance));
            }
        }

        return validRoutes;
    }

    private List<List<Float>> getMetricMatrix(List<GeographicPoint2d> coordinates, String metric) {
        String url = "https://api.openrouteservice.org/v2/matrix/foot-walking";
        List<List<Double>> transformed_coordinates = coordinates.stream().map(point -> List.of(point.getLongitude(), point.getLatitude())).toList();

        Map<String, Object> payload = new HashMap<>();
        payload.put("locations", transformed_coordinates);
        payload.put("metrics", List.of("distance", "duration") );

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
            JsonNode durations = root.path("durations");
            JsonNode distances = root.path("distances");

            System.out.println(durations);
            System.out.println(distances);

            List<List<Float>> distanceArray = mapper.convertValue(distances, new TypeReference<List<List<Float>>>() {});
            List<List<Float>> durationArray = mapper.convertValue(durations, new TypeReference<List<List<Float>>>() {});

            switch (metric) {
                case "distance":
                    return distanceArray;
                case "duration":
                    return durationArray;
                default:
                    System.out.println("Invalid metric: " + metric);
            }

        } catch (JsonProcessingException e) {
            System.out.println("Could not parse directions response.");
            return null;
        }

        return null;
    }

    private List<String> getInterestsFromDB(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<List> response = restTemplate.exchange(USERDB_URL + "userInterests/user_id=" + userId, HttpMethod.GET, entity, List.class);
        return response.getBody().stream().map(i ->((Map<String, Object>) i).get("interest_id").toString()).toList();
    }

    private List<Integer> getEntititesFromQdrant(List<String> interestsIds, String collectionName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        MatchRequest dto = new MatchRequest();
        dto.setCollectionName(collectionName);
        dto.setInterests(interestsIds);
        dto.setResultSize(10);

        System.out.println(dto);

        HttpEntity<MatchRequest> entity = new HttpEntity<>(dto, headers);
        ResponseEntity<List> response = restTemplate.exchange(QDRANT_URL + "categorize/match", HttpMethod.POST, entity, List.class);
        return response.getBody();
    }

    @Override
    public Map<String, Double> getDurationDistanceEstimate(double start_lat, double start_lng, double end_lat, double end_lng, List<ViennaHistoryWikiBuildingObject> stops) {
        List<GeographicPoint2d> points = new ArrayList<>();
        if(start_lat != 0.0 && start_lng != 0.0) {
            points.add(new GeographicPoint2d(start_lat, start_lng) );
        }
        stops.forEach(stop -> points.add(new GeographicPoint2d(stop.getLatitude().get(), stop.getLongitude().get())));
        if(end_lat != 0.0 && end_lng != 0.0) {
            points.add(new GeographicPoint2d(end_lat, end_lng) );
        }

        return accessOpenRoutingService(points, "foot-walking");
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
        stops.add(new GeographicPoint2d(tour.getStartLat(), tour.getStartLng()) );
        tour.getStops().forEach(stop -> stops.add(new GeographicPoint2d(stop.getLatitude().orElse(0.0), stop.getLongitude().orElse(0.0))));
        stops.add(new GeographicPoint2d(tour.getEndLat(), tour.getEndLng()));
        return accessOpenRoutingService(stops, "foot-walking");
    }


    private Map<String, Double> accessOpenRoutingService(List<GeographicPoint2d> points, String mode){
        //mode = foot-walking
        String url = "https://api.openrouteservice.org/v2/directions/" + mode + "/json";

        List<List<Double>> coordinates = points.stream().map(point -> List.of(point.getLongitude(), point.getLatitude())).toList();


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

    private static class RouteState {
        List<Integer> path;
        double distance;

        RouteState(List<Integer> path, double distance) {
            this.path = path;
            this.distance = distance;
        }
    }
}
