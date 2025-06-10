package group_05.ase.neo4j_data_access.Service.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import group_05.ase.neo4j_data_access.Entity.Tour.*;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Service.Interface.ITourService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.neo4j.types.GeographicPoint2d;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TourService implements ITourService {

    private HistoricBuildingService historicBuildingService;
    private HistoricPersonService historicPersonService;
    private HistoricEventService historicEventService;

    public TourService(HistoricBuildingService historicBuildingService, HistoricPersonService historicPersonService, HistoricEventService historicEventService) {
        this.historicBuildingService = historicBuildingService;
        this.historicPersonService = historicPersonService;
        this.historicEventService = historicEventService;
        this.mapper.registerModule(new Jdk8Module());
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final String USERDB_URL = "http://localhost:8090/";
    private final String QDRANT_URL = "http://localhost:8081/";
    private final Integer MAX_ROUTES = 10;

    //Multiset of persons that come on the tour, with index 0 = adults, 1=children, 2=seniors
    private final int[] person_configuration = {2,2,0};


    @Override
    public List<TourDTO> createTours(CreateTourRequestDTO dto) {

        //HTTP Request to User Interst DB
        List<String> interest_ids = getInterestsFromDB(dto.getUserId());
        System.out.println(interest_ids);

        //HTTP Request to QDRANT for related articles
        //TODO: Replace with extra service when ready
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
        System.out.println("Related buildings size: " + related_building_wikidata_ids.size());

        //Add to buildings that come into consideration for the tour
        buildings_wikidata_ids.addAll(related_building_wikidata_ids);
        buildings_wikidata_ids = buildings_wikidata_ids.stream().distinct().toList();

        List<GeographicPoint2d> stops = new ArrayList<>();



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

        List<List<Float>> distanceMatrix = getMetricMatrix(stops, "distance");
        System.out.println("Distance Matrix: " + distanceMatrix);

        Optional<GeographicPoint2d> startOptional;
        if(dto.getStart_lat() != 0.0 && dto.getStart_lng() != 0.0) {
            startOptional = Optional.of(new GeographicPoint2d(dto.getStart_lat(), dto.getStart_lng()));
        } else { startOptional = Optional.empty(); }

        Optional<GeographicPoint2d> endOptional;
        if(dto.getEnd_lng() != 0.0 && dto.getEnd_lat() != 0.0) {
            endOptional = Optional.of(new GeographicPoint2d(dto.getEnd_lat(), dto.getEnd_lng()));
        } else { endOptional = Optional.empty(); }
        List<GeographicPoint2d> mandatoryStops = dto.getPredefined_stops().stream().map(stop -> new GeographicPoint2d(stop.getLatitude().get(), stop.getLongitude().get())).toList();

        List<TourObject> routes = findRoutesBFS(startOptional, endOptional, mandatoryStops,  ,distanceMatrix);

        return tours.stream().map(this::tourObjectToTourDTO).toList();
    }

    private TourDTO tourObjectToTourDTO(TourObject tourObject) {
        TourDTO dto = new TourDTO();
        dto.setUserId(tourObject.getUserId());
        dto.setDescription(tourObject.getDescription());
        dto.setName(tourObject.getName());
        dto.setStart_lat(tourObject.getStartLat());
        dto.setStart_lng(tourObject.getStartLng());
        dto.setEnd_lat(tourObject.getEndLat());
        dto.setEnd_lng(tourObject.getEndLng());
        dto.setDistance(tourObject.getDistance());
        dto.setDurationEstimate(tourObject.getDurationEstimate());
        dto.setTourPrice(tourObject.getTourPrice());
        try{
            dto.setStops(mapper.writeValueAsString(tourObject.getStops()));
        } catch (JsonProcessingException e) {
            System.out.println("Could not convert stops array to JSON string: " + e.getMessage());
        }
        return dto;
    }


    private TourObject buildTourObject(List<GeographicPoint2d> stops, String userId, Map<GeographicPoint2d, Integer> building_dict, Double tourPrice) {
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



    public List<TourObject> findRoutesBFS(
            Optional<GeographicPoint2d> startOptional,
            Optional<GeographicPoint2d> endOptional,
            List<GeographicPoint2d> mandatoryStops,
            List<GeographicPoint2d> avaliableStops,
            List<List<Float>> distanceMatrix,
            CreateTourRequestDTO createTourRequestDTO,
            int maxRoutes,
            Map<GeographicPoint2d, Integer> building_dict
    ) {
        List<GeographicPoint2d> allStops = new ArrayList<>();

        startOptional.ifPresent(s -> {
            if (!allStops.contains(s)) allStops.add(s);
        });
        endOptional.ifPresent(e -> {
            if (!allStops.contains(e)) allStops.add(e);
        });

        for (GeographicPoint2d stop : mandatoryStops) {
            if (!allStops.contains(stop)) allStops.add(stop);
        }

        for (GeographicPoint2d stop : avaliableStops) {
            if (!allStops.contains(stop)) allStops.add(stop);
        }

        int N = allStops.size();

        int startIdx;
        int endIdx;

        if (startOptional.isPresent()) {
            startIdx = allStops.indexOf(startOptional.get());
            if (startIdx == -1) {
                throw new RuntimeException("Could not find start point: " + startOptional.get() + " in allStops");
            }
        } else {
            // If no explicit start, assume first stop in allStops as start
            startIdx = 0;
        }

        if (endOptional.isPresent()) {
            endIdx = allStops.indexOf(endOptional.get());
            if (endIdx == -1) {
                throw new RuntimeException("Could not find end point: " + endOptional.get() + " in allStops");
            }
        } else {
            endIdx = N - 1;
        }


        List<TourObject> validRoutes = new ArrayList<>();
        Queue<RouteState> queue = new LinkedList<>();

        List<Integer> initialPath = new ArrayList<>();
        initialPath.add(startIdx);

        Set<Integer> visitedMandatoryStops = new HashSet<>();
        for (int i = 0; i < mandatoryStops.size(); i++) {
            int mandatoryIdx = allStops.indexOf(mandatoryStops.get(i));
            if (initialPath.contains(mandatoryIdx)) {
                visitedMandatoryStops.add(mandatoryIdx);
            }
        }

        queue.add(new RouteState(initialPath, 0.0, visitedMandatoryStops));

        while (!queue.isEmpty()) {
            if (validRoutes.size() >= maxRoutes) break;

            RouteState state = queue.poll();
            List<Integer> path = state.path;
            double currentDistance = state.distance;
            Set<Integer> currentVisitedMandatoryStops = state.visitedMandatoryStops;

            int currentStopIdx = path.get(path.size() - 1);

            if (path.size() > createTourRequestDTO.getMinIntermediateStops() + 2) {
                continue; // Prune paths that are already too long
            }


            //Handle tours that reach the endIdx
            if (currentStopIdx == endIdx) {
                int intermediateStopsCount = path.size() - 2;

                boolean allMandatoryStopsVisited = true;
                for (GeographicPoint2d mandatoryStop : mandatoryStops) {
                    if (!path.contains(allStops.indexOf(mandatoryStop))) {
                        allMandatoryStopsVisited = false;
                        break;
                    }
                }

                if (allMandatoryStopsVisited &&
                        intermediateStopsCount == createTourRequestDTO.getMinIntermediateStops() &&
                        currentDistance >= createTourRequestDTO.getMinDistance() &&
                        currentDistance <= createTourRequestDTO.getMaxDistance()
                ) {
                    Map<Integer, List<PriceDTO>> pricePerStop = getPriceFromDB(path);
                    double currentPrice = 0;

                    for (Integer stopId : path) {
                        List<PriceDTO> pricesForLocation = pricePerStop.get(stopId);
                        if (pricesForLocation != null) {
                            Optional<PriceDTO> adultPriceDTO = pricesForLocation.stream().filter(dto -> "Adult".equals(dto.getName())).findFirst();
                            Optional<PriceDTO> childPriceDTO = pricesForLocation.stream().filter(dto -> "Child".equals(dto.getName())).findFirst();
                            Optional<PriceDTO> seniorPriceDTO = pricesForLocation.stream().filter(dto -> "Senior".equals(dto.getName())).findFirst();

                            if (adultPriceDTO.isPresent()) {
                                currentPrice += createTourRequestDTO.getPersonConfiguration()[0] * adultPriceDTO.get().getPrice();
                            }
                            if (childPriceDTO.isPresent()) {
                                currentPrice += createTourRequestDTO.getPersonConfiguration()[1] * childPriceDTO.get().getPrice();
                            }
                            if (seniorPriceDTO.isPresent()) {
                                currentPrice += createTourRequestDTO.getPersonConfiguration()[2] * seniorPriceDTO.get().getPrice();
                            }
                        }
                    }


                    if (currentPrice <= createTourRequestDTO.getMaxBudget()) {
                        List<GeographicPoint2d> routeGeographicPoints = path.stream()
                                .map(allStops::get)
                                .collect(Collectors.toList());

                        validRoutes.add(buildTourObject(routeGeographicPoints, createTourRequestDTO.getUserId(), building_dict, currentPrice));
                    }
                }
                continue; // Continue to next state in queue after processing a complete path
            }

            for (int nextStopIdx = 0; nextStopIdx < N; nextStopIdx++) {
                if (path.contains(nextStopIdx)) continue;

                if (path.size() + 1 > createTourRequestDTO.getMinIntermediateStops() + 2) {
                    continue;
                }

                double nextDistance = distanceMatrix.get(currentStopIdx).get(nextStopIdx);
                double newTotalDistance = currentDistance + nextDistance;

                if (newTotalDistance > createTourRequestDTO.getMaxDistance()) {
                    continue;
                }

                List<Integer> newPath = new ArrayList<>(path);
                newPath.add(nextStopIdx);

                Set<Integer> newVisitedMandatoryStops = new HashSet<>(currentVisitedMandatoryStops);
                for (GeographicPoint2d mandatoryStop : mandatoryStops) {
                    if (allStops.get(nextStopIdx).equals(mandatoryStop)) {
                        newVisitedMandatoryStops.add(nextStopIdx);
                    }
                }

                queue.add(new RouteState(newPath, newTotalDistance, newVisitedMandatoryStops));
            }
        }
        return validRoutes;
    }

    private static class RouteState {
        List<Integer> path;
        double distance;
        Set<Integer> visitedMandatoryStops; // Indices of mandatory stops visited in this path

        public RouteState(List<Integer> path, double distance, Set<Integer> visitedMandatoryStops) {
            this.path = path;
            this.distance = distance;
            this.visitedMandatoryStops = visitedMandatoryStops;
        }
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

    private Map<Integer, List<PriceDTO>> getPriceFromDB(List<Integer> location_ids) {
        ParameterizedTypeReference<List<List<PriceDTO>>> typeRef = new ParameterizedTypeReference<List<List<PriceDTO>>>() {};
        Map<Integer, List<PriceDTO>> resultMap = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>(location_ids.toString(), headers);
        ResponseEntity<List<List<PriceDTO>>> response = restTemplate.exchange(USERDB_URL + "prices/find/multiple", HttpMethod.GET, entity, typeRef);
        if(!response.getStatusCode().is2xxSuccessful()) {
         System.out.println("Error: " + response.getBody());
        }
        for(int i = 0; i<response.getBody().size(); i++){
            if(response.getBody().get(i).isEmpty()){
                continue;
            }
            resultMap.put(location_ids.get(i), response.getBody().get(i));
        }

        return resultMap;
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

            return Map.of("distance", distance/1000, "duration", duration/3600);

        } catch (JsonProcessingException e) {
            System.out.println("Could not parse directions response.");
            return null;
        }

    }
}
