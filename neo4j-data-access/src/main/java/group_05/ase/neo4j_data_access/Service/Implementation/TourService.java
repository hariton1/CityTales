package group_05.ase.neo4j_data_access.Service.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.ortools.sat.*;
import group_05.ase.neo4j_data_access.Client.QdrantClient;
import group_05.ase.neo4j_data_access.Client.UserDBClient;
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
    private UserDBClient userDBClient;
    private QdrantClient qdrantClient;

    public TourService(HistoricBuildingService historicBuildingService, UserDBClient userDBClient, QdrantClient qdrantClient) {
        this.historicBuildingService = historicBuildingService;
        this.userDBClient = userDBClient;
        this.qdrantClient = qdrantClient;
        this.mapper.registerModule(new Jdk8Module());
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final Integer MAX_ROUTES = 10;

    @Override
    public List<TourDTO> createTours(CreateTourRequestDTO dto) {

        List<Integer> interests = getInterestsFromDB(dto.getUserId());
        System.out.println(interests);
        List<Integer> buildingIds = getBuildingEntititesFromQdrant(interests.stream().map(Object::toString).toList());
        System.out.println("Building list size: " + buildingIds.size());

        List<GeographicPoint2d> stops = new ArrayList<>();
        Map<GeographicPoint2d, Integer> building_dict = new HashMap<>();

        for(Integer stopId: buildingIds) {
            ViennaHistoryWikiBuildingObject viennaHistoryWikiBuildingObject = historicBuildingService.getBuildingById(stopId);
            if(viennaHistoryWikiBuildingObject.getLatitude().isEmpty() || viennaHistoryWikiBuildingObject.getLongitude().isEmpty() ) {
                continue;
            }
            GeographicPoint2d stop = new GeographicPoint2d(viennaHistoryWikiBuildingObject.getLatitude().get(), viennaHistoryWikiBuildingObject.getLongitude().get());
            building_dict.put(stop, viennaHistoryWikiBuildingObject.getViennaHistoryWikiId());
            System.out.println(stop);
            stops.add(stop);
        }
        System.out.println("Stops array size: "+ stops.size());


        //assert that all predefined stops are in the stops list, should be anyway since qdrant returns all buildings
        dto.getPredefinedStops().forEach(stop -> {assert stops.contains(new GeographicPoint2d(stop.getLatitude().get(), stop.getLongitude().get()));});


        Optional<GeographicPoint2d> startOptional;
        Optional<GeographicPoint2d> endOptional;
        if(dto.getStart_lat() != 0.0 && dto.getStart_lng() != 0.0) {
            startOptional = Optional.of(new GeographicPoint2d(dto.getStart_lat(), dto.getStart_lng()));
        } else { startOptional = Optional.empty(); }
        if(dto.getEnd_lng() != 0.0 && dto.getEnd_lat() != 0.0) {
            endOptional = Optional.of(new GeographicPoint2d(dto.getEnd_lat(), dto.getEnd_lng()));
        } else { endOptional = Optional.empty(); }

        if(startOptional.isPresent()) {
            //Prepend start
            stops.add(0, startOptional.get());
        }
        if(endOptional.isPresent()) {
            //Append end
            stops.add(endOptional.get());
        }


        List<List<Float>> distanceMatrix = getMetricMatrix(stops, "distance");

        List<GeographicPoint2d> mandatoryStops = dto.getPredefinedStops().stream().map(stop -> new GeographicPoint2d(stop.getLatitude().get(), stop.getLongitude().get())).toList();

        //List<TourObject> foundTours = findRoutesBFS(startOptional, endOptional, mandatoryStops, stops  ,distanceMatrix, dto, MAX_ROUTES, building_dict);

        findRoutesSATSolving(distanceMatrix);

        // System.out.println("Found " + foundTours.size() + " tours");

        //return foundTours.stream().map(this::tourObjectToTourDTO).toList();
        return null;
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


    private TourObject buildTourObject(List<GeographicPoint2d> stops, String userId, Map<GeographicPoint2d, Integer> building_dict, Double tourPrice, Map<Integer, List<PriceDTO>> pricePerStop) {
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
        tourObject.setPricePerStop(pricePerStop);

        return tourObject;
    }

    public void findRoutesSATSolving(List<List<Float>> distanceMatrix) {
        System.out.println("Called findRoutesSATSolving");

        // ─────────────────────────  INPUT DATA  ──────────────────────────
        /** index 0 = start, 1 = end, 2..N-1 = candidate stops */
        double[][] distanceMatrixArray = new double[distanceMatrix.size()][distanceMatrix.get(0).size()];
        for(int i = 0; i < distanceMatrix.size(); i++) {
            for(int j = 0; j < distanceMatrix.get(i).size(); j++) {
                distanceMatrixArray[i][j] = distanceMatrix.get(i).get(j);
            }
        }



        final double[][] DIST = distanceMatrixArray;

        /** price[i] = entrance fee (0 for start/end) */
        final double[] PRICE = { /* <-- fill prices for each location */ };

        /** how many intermediate stops you want (fixed) */
        final int K = 4;                            // example: 4 stops

        /** maximum total distance allowed for the tour */
        final double MAX_DISTANCE = 10000;         // metres, km,…

        /** maximum total budget allowed for the tour */
        final double MAX_BUDGET   =  50.0;

        //System.loadLibrary("jniortools");                // native loader
        int N = DIST.length;                             // number of nodes

        CpModel model = new CpModel();

        // ---- 1. Decision variables -----------------------------------
        // x[i][j] = 1 if we travel directly i -> j
        BoolVar[][] x = new BoolVar[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                x[i][j] = model.newBoolVar("x_" + i + "_" + j);

        // pos[i] = order position of node i along the path (only for subtour cut)
        IntVar[] pos = new IntVar[N];
        for (int i = 0; i < N; i++)
            pos[i] = model.newIntVar(0, N - 1, "pos_" + i);

        // ---- 2. Path-building constraints ----------------------------
        int START = 0, END = 1;

        // Each node has at most one outgoing arc
        for (int i = 0; i < N; i++) {
            LinearExpr outgoing = LinearExpr.sum(x[i]);     // Σ_j x[i][j]
            if (i == END)
                model.addEquality(outgoing, 0);               // end has none
            else if (i == START)
                model.addEquality(outgoing, 1);               // start has exactly 1
            else
                model.addLessOrEqual(outgoing, 1);            // normal node ≤ 1
        }

        // Each node has at most one incoming arc
        for (int j = 0; j < N; j++) {
            LinearExpr incoming = sumColumn(x, j);
            if (j == START)
                model.addEquality(incoming, 0);               // start has none
            else if (j == END)
                model.addEquality(incoming, 1);               // end has exactly 1
            else
                model.addLessOrEqual(incoming, 1);            // normal node ≤ 1
        }

        // Exactly K intermediate nodes must be visited
        IntVar[] visit = new IntVar[N];
        for (int i = 0; i < N; i++)
            visit[i] = model.newBoolVar("visit_" + i);

        // visit[i] is 1 if either we enter or leave i (except start/end)
//        for (int i = 2; i < N; i++)
//            model.addEquality(
//                    LinearExpr.sum(new IntVar[]{sumRow(x, i), sumColumn(x, i)}),
//                    LinearExpr.term(visit[i], 1));

        // total #visited = K
        model.addEquality(LinearExpr.sum(Arrays.copyOfRange(visit, 2, N)), K);

//        // ---- 3. Subtour elimination  (Miller–Tucker–Zemlin) ----------
//        // pos[START] = 0
//        model.addEquality(pos[START], 0);
//        // pos[END] = K+1
//        model.addEquality(pos[END], K + 1);
//
//        for (int i = 0; i < N; i++)
//            for (int j = 0; j < N; j++)
//                if (i != j)
//                    // If x[i][j]==1 then pos[j] = pos[i] + 1
//                    model.addEquality(pos[j], LinearExpr.sum(pos[i], 1))
//                            .onlyEnforceIf(x[i][j]);

        // ---- 4. Distance and budget constraints ----------------------
        LinearExpr totalDistance = LinearExpr.weightedSum(flatten(x), flattenDistances(DIST));
        model.addLessOrEqual(totalDistance, (long) Math.round(MAX_DISTANCE));

//        LinearExpr totalBudget = LinearExpr.weightedSum(visit, doubleToLong(PRICE));
//        model.addLessOrEqual(totalBudget, (long) Math.round(MAX_BUDGET * 100)); // scale if needed

        // ---- 5. Objective (optional – e.g., minimise distance) -------
        model.minimize(totalDistance);

        // ---- 6. Solve -------------------------------------------------
        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(30);
        CpSolverStatus status = solver.solve(model);

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.println("Total distance = " + solver.value(totalDistance));
//            System.out.println("Total budget   = " + solver.value(totalBudget) / 100.0);
            printPath(solver, x, START, END);
        } else {
            System.out.println("No tour found under given constraints.");
        }



    }

    private LinearExpr sumRow(IntVar[][] m, int r) {
        return LinearExpr.sum(m[r]);
    }

    private LinearExpr sumColumn(IntVar[][] m, int c) {
        IntVar[] col = new IntVar[m.length];
        for (int i = 0; i < m.length; i++) col[i] = m[i][c];
        return LinearExpr.sum(col);
    }

    private IntVar[] flatten(IntVar[][] matrix) {
        return Arrays.stream(matrix).flatMap(Arrays::stream).toArray(IntVar[]::new);
    }

    private long[] flattenDistances(double[][] d) {
        return Arrays.stream(d)
                .flatMapToDouble(Arrays::stream)
                .mapToLong(Math::round)
                .toArray();
    }

    private long[] doubleToLong(double[] arr) {
        long[] out = new long[arr.length];
        for (int i = 0; i < arr.length; i++) out[i] = Math.round(arr[i] * 100); // 2-decimal cents
        return out;
    }

    private void printPath(CpSolver s, BoolVar[][] x, int start, int end) {
        int n = x.length, current = start;
        System.out.print("Path: " + current);
        while (current != end) {
            for (int j = 0; j < n; j++) {
                if (s.booleanValue(x[current][j])) {
                    current = j;
                    System.out.print(" -> " + current);
                    break;
                }
            }
        }
        System.out.println();
    }



    public List<TourObject> findRoutesBFS(
            Optional<GeographicPoint2d> startOptional,
            Optional<GeographicPoint2d> endOptional,
            List<GeographicPoint2d> mandatoryStops,
            List<GeographicPoint2d> availableStops,
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

        for (GeographicPoint2d stop : availableStops) {
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
            System.out.println("Current RAM Usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000000);
            if (validRoutes.size() >= maxRoutes) break;

            RouteState state = queue.poll();
            List<Integer> path = state.path;
            double currentDistance = state.distance;
            Set<Integer> currentVisitedMandatoryStops = state.visitedMandatoryStops;

            int currentStopIdx = path.get(path.size() - 1);

            if (path.size() > createTourRequestDTO.getNumStops() + 2) {
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
                        intermediateStopsCount == createTourRequestDTO.getNumStops() &&
                        currentDistance >= createTourRequestDTO.getMinDistance() &&
                        currentDistance <= createTourRequestDTO.getMaxDistance()
                ) {
                    List<Integer> pathBuildingIds = path.stream().map(index -> building_dict.get(availableStops.get(index))).filter(Objects::nonNull).toList();
                    Map<Integer, List<PriceDTO>> pricePerStop = getPriceFromDB(pathBuildingIds);
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

                        validRoutes.add(buildTourObject(routeGeographicPoints, createTourRequestDTO.getUserId(), building_dict, currentPrice, pricePerStop));
                    }
                }
                continue; // Continue to next state in queue after processing a complete path
            }

            for (int nextStopIdx = 0; nextStopIdx < N; nextStopIdx++) {
                if (path.contains(nextStopIdx)) continue;

                if (path.size() + 1 > createTourRequestDTO.getNumStops() + 2) {
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

    private List<Integer> getInterestsFromDB(String userId) {
        return this.userDBClient.getUserInterestsIds(UUID.fromString(userId));
    }

    private Map<Integer, List<PriceDTO>> getPriceFromDB(List<Integer> location_ids) {
        System.out.println("Prices for location ids: " + location_ids);
        List<PriceDTO> prices = this.userDBClient.getPricesFromDB(location_ids);
        if(prices.isEmpty()) {
         System.out.println("Error: Could not retrieve prices from User DB! Either there are no prices available, or there was an issue with the DB connection.");
        }
        Map<Integer, List<PriceDTO>> priceMap = new HashMap<>();
        for(Integer location_id : prices.stream().map(PriceDTO::getLocationId).distinct().toList()) {
            List<PriceDTO> pricesToLocation = prices.stream().filter(priceDTO -> priceDTO.getLocationId() == location_id).toList();
            priceMap.put(location_id, pricesToLocation);
        }

        return priceMap;
    }

    private List<Integer> getBuildingEntititesFromQdrant(List<String> interestsIds) {
        MatchRequest dto = new MatchRequest();
        dto.setCollectionName("WienGeschichteWikiBuildings");
        dto.setInterests(interestsIds);
        dto.setResultSize(50);
        return qdrantClient.getBuildingEntityIdsFromQdrant(dto);
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
