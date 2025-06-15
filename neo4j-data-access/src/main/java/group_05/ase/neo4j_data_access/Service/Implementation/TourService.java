package group_05.ase.neo4j_data_access.Service.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import group_05.ase.neo4j_data_access.Client.QdrantClient;
import group_05.ase.neo4j_data_access.Client.UserDBClient;
import group_05.ase.neo4j_data_access.Entity.Tour.*;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Service.Interface.ITourService;
import org.springframework.data.neo4j.types.GeographicPoint2d;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
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
    private final Integer MAX_NEARBY_BUILDINGS = 30;
    private final Double MAX_RAM_PERCENT = 0.5;

    @Override
    public List<TourDTO> createTours(CreateTourRequestDTO dto) {

        List<Integer> interests = getInterestsFromDB(dto.getUserId());
        //List<Integer> interests = List.of(2,3,4,5,6,7,10,12);
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

        assert(startOptional.isPresent());

        if(startOptional.isPresent()) {
            //Prepend start
            stops.add(0, startOptional.get());
        }
        if(endOptional.isPresent()) {
            //Append end
            stops.add(endOptional.get());
        }


        List<List<Float>> distanceMatrix = getMetricMatrix(stops, "distance");
        if(distanceMatrix == null) {
            System.out.println("Distance matrix is null");
            return null;
        }

        List<GeographicPoint2d> mandatoryStops = dto.getPredefinedStops().stream().map(stop -> new GeographicPoint2d(stop.getLatitude().get(), stop.getLongitude().get())).toList();


        //First: Try with breadth first search
        List<TourObject> foundTours = findRoutesBFS(startOptional, endOptional, mandatoryStops, stops, distanceMatrix, dto, MAX_ROUTES, building_dict);
        if(foundTours.isEmpty()){
            System.out.println("No tours found with BFS! Trying with Graphhopper Logic...");
        } else {
            System.out.println("Found " + foundTours.size() + " tours with BFS!");
            return foundTours.stream().map(this::tourObjectToTourDTO).collect(Collectors.toList());
        }

        List<Integer> foundTour = findRoutesGraphhopper(distanceMatrix, dto.getNumStops(), endOptional.isPresent(), dto.getMaxDistance(), dto.getMaxBudget(), stops, building_dict, dto);
        System.out.println("Found " + foundTour.size() + " tours with Graphhopper!");
        if(!foundTour.isEmpty()){
            System.out.println("Found tour with Graphhopper:" + foundTour);
            List<GeographicPoint2d> stopsNew = new ArrayList<>();
            foundTour.forEach(stop -> stopsNew.add(stops.get(stop)));
            double tourPrice = getTotalPriceForTour(foundTour, stops, building_dict, dto);
            Map<Integer, List<PriceDTO>> pricePerStop = getPricePerStop(foundTour, stops, building_dict, dto);
            TourObject tourObject = buildTourObject(stopsNew, dto.getUserId(), building_dict, tourPrice, pricePerStop);
            return List.of(tourObjectToTourDTO(tourObject));
        } else{
            System.out.println("No tours found with Graphhopper!");
            return List.of();
        }
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

    private List<Integer> findRoutesGraphhopper(List<List<Float>> distanceMatrix,
                                                int numberOfStops,
                                                boolean stopDefined,
                                                double maxDistance,
                                                double maxBudget,
                                                List<GeographicPoint2d> stops,
                                                Map<GeographicPoint2d, Integer> building_dict,
                                                CreateTourRequestDTO dto) {
        double[][] distanceMatrice = new double[distanceMatrix.size()][distanceMatrix.get(0).size()];
        for(int i = 0; i < distanceMatrix.size(); i++) {
            for(int j = 0; j < distanceMatrix.get(i).size(); j++) {
                distanceMatrice[i][j] = distanceMatrix.get(i).get(j);
            }
        }


        int desiredNumberOfServicesPerVehicle = numberOfStops;
        int STOP_COUNT_DIMENSION_INDEX = 0;

        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType");
        vehicleTypeBuilder.addCapacityDimension(STOP_COUNT_DIMENSION_INDEX, desiredNumberOfServicesPerVehicle);
        VehicleTypeImpl vehicleType = vehicleTypeBuilder.build();

        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");

        Location start = Location.newInstance("0");
        vehicleBuilder.setStartLocation(start);

        if(stopDefined) {
            Location end = Location.newInstance(String.valueOf(distanceMatrice.length -1));
            vehicleBuilder.setEndLocation(end).setReturnToDepot(true);
        }

        vehicleBuilder.setType(vehicleType).setReturnToDepot(false);



        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicleBuilder.build());

        // Add all service locations (except depot)
        for (int i = 1; i < distanceMatrix.size() - 1; i++) { // Assuming 0 is start, N-1 is end
            vrpBuilder.addJob(com.graphhopper.jsprit.core.problem.job.Service.Builder.newInstance("stop_" + i)
                    .setLocation(Location.newInstance(String.valueOf(i)))
                    // Assign a demand of 1 for our STOP_COUNT_DIMENSION_INDEX
                    .addSizeDimension(STOP_COUNT_DIMENSION_INDEX, 1)
                    .build());
        }

        // Use your own distance matrix
        vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
        vrpBuilder.setRoutingCost(new CustomMatrixCost(distanceMatrice));
        VehicleRoutingProblem problem = vrpBuilder.build();

        // Solve
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

        // Extract best solution
        System.out.println("Found " + solutions.size() + " solutions to routing problem! ");

        List<List<Integer>> possibleRoutes = new ArrayList<>();

        for (VehicleRoutingProblemSolution solution : solutions) {
            //Filter best routes
            System.out.println("Solution cost: " + solution.getCost());
            List<List<Integer>> paths = new ArrayList<>();
            for (VehicleRoute route: solution.getRoutes()){
                List<Integer> routeDetail = new ArrayList<>();
                routeDetail.add(Integer.valueOf(route.getStart().getLocation().getId()));
                routeDetail.addAll(route.getActivities().stream().map(tourActivity -> Integer.parseInt(tourActivity.getLocation().getId())).toList());
                double tourCost = getTotalPriceForTour(routeDetail, stops, building_dict, dto);
                double tourDistance = getTourDistance(routeDetail, distanceMatrix);
                System.out.println("Calculated tour cost for tour:" + tourCost);
                System.out.println("Calculated tour distance for tour:" + tourDistance);
                System.out.println("Max tour distance for tour:" + maxDistance);
                if (tourCost > maxBudget){
                    continue;
                }
                if (tourDistance > maxDistance){
                    continue;
                }
                paths.add(routeDetail);
            }
            possibleRoutes.addAll(paths);
        }
        System.out.println("Found routes " + possibleRoutes.size());
        if(possibleRoutes.isEmpty()){
            return List.of();
        } else{
            return possibleRoutes.get(0);
        }
    }

    private double getTotalPriceForTour(List<Integer> path, List<GeographicPoint2d> stops, Map<GeographicPoint2d, Integer> building_dict, CreateTourRequestDTO dto) {
        List<Integer> pathBuildingIds = path.stream().map(index -> building_dict.get(stops.get(index))).toList();
        Map<Integer, List<PriceDTO>> pricePerStop = getPriceFromDB(pathBuildingIds);
        double currentPrice = 0;

        for (Integer stopId : path) {
            List<PriceDTO> pricesForLocation = pricePerStop.get(stopId);
            if (pricesForLocation != null) {
                Optional<PriceDTO> adultPriceDTO = pricesForLocation.stream().filter(priceDto -> "Adult".equals(priceDto.getName())).findFirst();
                Optional<PriceDTO> childPriceDTO = pricesForLocation.stream().filter(priceDto -> "Child".equals(priceDto.getName())).findFirst();
                Optional<PriceDTO> seniorPriceDTO = pricesForLocation.stream().filter(priceDto -> "Senior".equals(priceDto.getName())).findFirst();

                if (adultPriceDTO.isPresent()) {
                    currentPrice += dto.getPersonConfiguration()[0] * adultPriceDTO.get().getPrice();
                }
                if (childPriceDTO.isPresent()) {
                    currentPrice += dto.getPersonConfiguration()[1] * childPriceDTO.get().getPrice();
                }
                if (seniorPriceDTO.isPresent()) {
                    currentPrice += dto.getPersonConfiguration()[2] * seniorPriceDTO.get().getPrice();
                }
            }
        }

        return currentPrice;
    }

    private Map<Integer, List<PriceDTO>> getPricePerStop(List<Integer> path, List<GeographicPoint2d> stops, Map<GeographicPoint2d, Integer> building_dict, CreateTourRequestDTO dto) {
        List<Integer> pathBuildingIds = path.stream().map(index -> building_dict.get(stops.get(index))).toList();
        Map<Integer, List<PriceDTO>> pricePerStop = getPriceFromDB(pathBuildingIds);
        Map<Integer, List<PriceDTO>> filteredPricePerStop = new HashMap<>();
        double currentPrice = 0;

        for (Integer stopId : path) {
            List<PriceDTO> pricesForLocation = pricePerStop.get(stopId);
            if (pricesForLocation != null) {
                Optional<PriceDTO> adultPriceDTO = pricesForLocation.stream().filter(priceDto -> "Adult".equals(priceDto.getName())).findFirst();
                Optional<PriceDTO> childPriceDTO = pricesForLocation.stream().filter(priceDto -> "Child".equals(priceDto.getName())).findFirst();
                Optional<PriceDTO> seniorPriceDTO = pricesForLocation.stream().filter(priceDto -> "Senior".equals(priceDto.getName())).findFirst();

                List<PriceDTO> prices = new ArrayList<>();

                adultPriceDTO.ifPresent(prices::add);
                childPriceDTO.ifPresent(prices::add);
                seniorPriceDTO.ifPresent(prices::add);
                filteredPricePerStop.put(stopId, prices);
            }
        }
        return filteredPricePerStop;
    }


    private double getTourDistance(List<Integer> stops, List<List<Float>> distanceMatrix){
        double totalCost = 0;
        for (int i = 0; i < stops.size() - 1; i++) {
            totalCost += distanceMatrix.get(stops.get(i)).get(stops.get(i + 1));
        }
        return totalCost;
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
            double ram_usage = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime.getRuntime().totalMemory();
            System.out.println("Current VM RAM Usage: " + ram_usage + "%");
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
                    double currentPrice = getTotalPriceForTour(path, allStops, building_dict, createTourRequestDTO);
                    System.out.println("Current price: " + currentPrice);

                    if (currentPrice <= createTourRequestDTO.getMaxBudget()) {
                        List<GeographicPoint2d> routeGeographicPoints = path.stream()
                                .map(allStops::get)
                                .collect(Collectors.toList());

                        Map<Integer, List<PriceDTO>> pricePerStop = getPricePerStop(path, allStops, building_dict, createTourRequestDTO);

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
        ResponseEntity<String> response;
        try{
            response = restTemplate.postForEntity(url, request, String.class);
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
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
        dto.setResultSize(MAX_NEARBY_BUILDINGS);
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
