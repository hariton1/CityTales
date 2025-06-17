package group_05.ase.neo4j_data_access.Service.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.IntStream;

@Service
public class TourService implements ITourService {

    Logger logger = LoggerFactory.getLogger(TourService.class);

    private final HistoricBuildingService historicBuildingService;
    private final UserDBClient userDBClient;
    private final QdrantClient qdrantClient;

    public TourService(HistoricBuildingService historicBuildingService, UserDBClient userDBClient, QdrantClient qdrantClient) {
        this.historicBuildingService = historicBuildingService;
        this.userDBClient = userDBClient;
        this.qdrantClient = qdrantClient;
        this.mapper.registerModule(new Jdk8Module());
    }

    private final ObjectMapper mapper = new ObjectMapper();

    private final Integer MAX_ROUTES = 100;
    private final Integer MAX_NEARBY_BUILDINGS = 30;

    @Override
    public List<TourDTO> createTours(CreateTourRequestDTO dto) {

        if(dto.getStart_lat() == 0.0 || dto.getStart_lng() == 0.0 || dto.getEnd_lat() == 0.0 || dto.getEnd_lng() == 0.0) {
            logger.error("Tour Creation Failed: No start/end coordinates supplied!");
            return Collections.emptyList();
        }



        List<Integer> interests = getInterestsFromDB(dto.getUserId());
        List<Integer> buildingIds = getBuildingEntititesFromQdrant(interests.stream().map(Object::toString).toList());

        Deque<GeographicPoint2d> stops = new ArrayDeque<>();
        Map<GeographicPoint2d, Integer> building_dict = new HashMap<>();

        logger.info("Fetching price information from db...");
        Map<Integer, List<PriceDTO>> pricePerBuilding = getPriceFromDB(buildingIds);

        for(Integer stopId: buildingIds) {
            ViennaHistoryWikiBuildingObject viennaHistoryWikiBuildingObject = historicBuildingService.getBuildingById(stopId);
            if(viennaHistoryWikiBuildingObject.getLatitude().isEmpty() || viennaHistoryWikiBuildingObject.getLongitude().isEmpty() ) {
                continue;
            }
            GeographicPoint2d stop = new GeographicPoint2d(viennaHistoryWikiBuildingObject.getLatitude().get(), viennaHistoryWikiBuildingObject.getLongitude().get());
            building_dict.put(stop, viennaHistoryWikiBuildingObject.getViennaHistoryWikiId());
            stops.add(stop);
        }


        //assert that all predefined stops are in the stops list, should be anyway since qdrant returns all buildings
        dto.getPredefinedStops().forEach(stop -> {assert stops.contains(new GeographicPoint2d(stop.getLatitude().get(), stop.getLongitude().get()));});


        //Prepend start and append end
        stops.addFirst(new GeographicPoint2d(dto.getStart_lat(), dto.getStart_lng()));
        stops.add(new GeographicPoint2d(dto.getEnd_lat(), dto.getEnd_lng()));

        List<List<Float>> distanceMatrix = getMetricMatrix(stops.stream().toList(), "distance");
        if(distanceMatrix == null) {
            logger.error("Distance matrix is null! Return null");
            return null;
        }

        List<List<Float>> durationMatrix = getMetricMatrix(stops.stream().toList(), "duration");
        if(durationMatrix == null) {
            logger.error("Duration matrix is null! Return null");
            return null;
        }

        List<GeographicPoint2d> mandatoryStops = dto.getPredefinedStops().stream().map(stop -> new GeographicPoint2d(stop.getLatitude().get(), stop.getLongitude().get())).toList();

        List<Integer> stopsIndexes = IntStream.range(0, stops.size()).boxed().toList();
        Set<Integer> mandatoryIndexes = new HashSet<>();
        for(GeographicPoint2d mandatoryStop: mandatoryStops) {
            mandatoryIndexes.add(stops.stream().toList().indexOf(mandatoryStop));
        }

        List<List<Integer>> newRoutes = findToursBFS(stopsIndexes, distanceMatrix, dto.getMaxDistance(), dto.getNumStops(), mandatoryIndexes, MAX_ROUTES, dto.getMaxBudget(), dto, building_dict, stops.stream().toList(), pricePerBuilding);
        List<TourObject> foundTours = indiceListToObjectList(newRoutes, stops.stream().toList(), building_dict, distanceMatrix, dto, pricePerBuilding, durationMatrix);

        if(foundTours.isEmpty()){
                logger.error("No tours found with BFS! Trying with Graphhopper Logic...");
        } else {
            logger.info("Found {} tours with BFS!", foundTours.size());
            return foundTours.stream().map(this::tourObjectToTourDTO).collect(Collectors.toList());
        }

        List<Integer> foundTour = findRoutesGraphhopper(distanceMatrix, dto.getNumStops(), dto.getMaxDistance(), dto.getMaxBudget(), stops.stream().toList(), building_dict, dto, pricePerBuilding);
        logger.info("Found {} tours with Graphhopper!", foundTour.size());
        if(!foundTour.isEmpty()){
            List<GeographicPoint2d> stopsNew = new ArrayList<>();
            foundTour.forEach(stop -> stopsNew.add(stops.stream().toList().get(stop)));
            double tourPrice = getTotalPriceForTour(foundTour, stops.stream().toList(), building_dict, dto, pricePerBuilding);
            Map<Integer, List<PriceDTO>> pricePerStop = getPricePerStop(foundTour, stops.stream().toList(), building_dict, dto, pricePerBuilding);
            TourObject tourObject = buildTourObject(stopsNew, dto.getUserId(), building_dict, tourPrice, pricePerStop);
            return List.of(tourObjectToTourDTO(tourObject));
        } else{
            logger.error("No tours found with Graphhopper!");
            return List.of();
        }
    }

    public List<List<Integer>> findToursBFS(List<Integer> stops,
                                            List<List<Float>> distanceMatrix,
                                            Double maxDistance,
                                            Integer noStops,
                                            Set<Integer> mandatoryStops,
                                            Integer maxRoutes,
                                            Double maxTourPrice,
                                            CreateTourRequestDTO dto,
                                            Map<GeographicPoint2d, Integer> buildingDict,
                                            List<GeographicPoint2d> stopLocations,
                                            Map<Integer, List<PriceDTO>> pricePerStop) {
        Integer startIndex = stops.get(0);
        Integer endIndex = stops.get(stops.size() - 1);

        List<List<Integer>> validRoutes = new ArrayList<>();
        Queue<RouteState> queue = new LinkedList<>();
        for(int i=0; i<stops.size(); i++){
            if(i == startIndex){
                //start, continue
                continue;
            }

            if(i == endIndex){
                if(distanceMatrix.get(startIndex).get(endIndex) > maxDistance){
                    continue;
                } else if (noStops == 0){
                    validRoutes.add(List.of(startIndex, endIndex));
                }
            }

            //Normal case
            Set<Integer> visitedMandatoryStops = new HashSet<>();
            if(mandatoryStops.contains(i)){
                visitedMandatoryStops.add(i);
            }
            RouteState state = new RouteState(new ArrayList<>(List.of(startIndex, i)), distanceMatrix.get(startIndex).get(i), visitedMandatoryStops);
            queue.add(state);
        }

        while(!queue.isEmpty()){
            RouteState state = queue.poll();
            if(state.path.size() -2 > noStops){continue;}
            if(getTourDistance(state.path, distanceMatrix) > maxDistance){continue;}

            for(int i=0; i< stops.size(); i++){
                if(state.path.contains(i) || i == startIndex){continue;}
                List<Integer> newPath = new ArrayList<>(state.path);
                newPath.add(i);

                double newDistance = getTourDistance(newPath, distanceMatrix);
                Set<Integer> newVisitedMandatoryStops = new HashSet<>(state.visitedMandatoryStops);

                if (mandatoryStops.contains(i)) {
                    newVisitedMandatoryStops.add(i);
                }

                if (i == endIndex) {
                    if (newPath.size() - 2 == noStops &&
                            newDistance <= maxDistance &&
                            newVisitedMandatoryStops.equals(mandatoryStops)
                            && validRoutes.size() < maxRoutes ) {
                        double totalTourPrice = getTotalPriceForTour(state.path, stopLocations, buildingDict, dto, pricePerStop);
                        if(totalTourPrice <= maxTourPrice){
                            validRoutes.add(newPath);
                            logger.info("Valid routes: {}", validRoutes.toString());
                        }
                    }
                } else {
                    queue.add(new RouteState(newPath, newDistance, newVisitedMandatoryStops));
                }
            }
        }
        return validRoutes;
    }

    public List<Integer> findRoutesGraphhopper(List<List<Float>> distanceMatrix,
                                               int numberOfStops,
                                               double maxDistance,
                                               double maxBudget,
                                               List<GeographicPoint2d> stops,
                                               Map<GeographicPoint2d, Integer> building_dict,
                                               CreateTourRequestDTO dto,
                                               Map<Integer, List<PriceDTO>> pricePerStop) {
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
        Location end = Location.newInstance(String.valueOf(distanceMatrice.length -1));

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

        List<List<Integer>> possibleRoutes = new ArrayList<>();

        for (VehicleRoutingProblemSolution solution : solutions) {
            //Filter best routes
            List<List<Integer>> paths = new ArrayList<>();
            for (VehicleRoute route: solution.getRoutes()){
                List<Integer> routeDetail = new ArrayList<>();
                routeDetail.add(Integer.valueOf(route.getStart().getLocation().getId()));
                routeDetail.addAll(route.getActivities().stream().map(tourActivity -> Integer.parseInt(tourActivity.getLocation().getId())).toList());
                double tourCost = getTotalPriceForTour(routeDetail, stops, building_dict, dto, pricePerStop);
                double tourDistance = getTourDistance(routeDetail, distanceMatrix);
                logger.info("Calculated tour cost for tour:{}", tourCost);
                logger.info("Calculated tour distance for tour:{}", tourDistance);
                logger.info("Max tour distance for tour:{}", maxDistance);
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
        logger.info("Found routes with Graphhopper: {}", possibleRoutes.size());
        if(possibleRoutes.isEmpty()){
            return List.of();
        } else{
            return possibleRoutes.get(0);
        }
    }

    private List<TourObject> indiceListToObjectList(List<List<Integer>> tours, List<GeographicPoint2d> stops, Map<GeographicPoint2d, Integer> building_dict, List<List<Float>> distanceMatrix, CreateTourRequestDTO dto, Map<Integer, List<PriceDTO>> pricePerStop, List<List<Float>> durationMatrix) {
        List<TourObject> tourObjects = new ArrayList<>();
        for(List<Integer> tour: tours){
            TourObject tourObject = new TourObject();
            tourObject.setStartLat(stops.get(0).getLatitude());
            tourObject.setStartLng(stops.get(0).getLongitude());
            tourObject.setEndLat(stops.get(tour.size()-1).getLatitude());
            tourObject.setEndLng(stops.get(tour.size()-1).getLongitude());
            tourObject.setDistance(getTourDistance(tour, distanceMatrix) / 1000);
            tourObject.setDurationEstimate(getTourDuration(tour, durationMatrix) / 3600);
            tourObject.setName("Tour from " + Date.from(Instant.now()));
            tourObject.setDescription("Tour created at " + Date.from(Instant.now()));
            List<ViennaHistoryWikiBuildingObject> stops_list = new ArrayList<>();
            for(Integer stop : tour) {
                if(tour.indexOf(stop) == 0 || tour.indexOf(stop) == tour.size()-1){continue;}
                ViennaHistoryWikiBuildingObject building = historicBuildingService.getBuildingById(building_dict.get(stops.get(stop)));
                stops_list.add(building);
            }
            tourObject.setStops(stops_list);
            tourObject.setTourPrice(getTotalPriceForTour(tour, stops, building_dict, dto, pricePerStop));
            tourObject.setPricePerStop(getPricePerStop(tour, stops, building_dict, dto, pricePerStop));
            tourObject.setUserId(dto.getUserId());

            tourObjects.add(tourObject);
        }
        return tourObjects;
    }

    private TourDTO tourObjectToTourDTO(TourObject tourObject) {
        mapper.registerModule(new JavaTimeModule());

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
            dto.setPricePerStop(mapper.writeValueAsString(tourObject.getPricePerStop()));
        } catch (JsonProcessingException e) {
            logger.error("Could not convert stops array/price per stop to JSON string: {}", e.getMessage());
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
        tourObject.setTourPrice(tourPrice);
        tourObject.setPricePerStop(pricePerStop);

        return tourObject;
    }



    private double getTotalPriceForTour(List<Integer> path,
                                        List<GeographicPoint2d> stops,
                                        Map<GeographicPoint2d, Integer> building_dict,
                                        CreateTourRequestDTO dto,
                                        Map<Integer, List<PriceDTO>> pricePerStop) {
        logger.info("Total price for tour path: {}", path.toString());
        List<Integer> pathBuildingIds = path.stream().map(index -> building_dict.get(stops.get(index))).toList();
        double currentPrice = 0;

        for (Integer stopId : pathBuildingIds) {
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

    private Map<Integer, List<PriceDTO>> getPricePerStop(List<Integer> path, List<GeographicPoint2d> stops, Map<GeographicPoint2d, Integer> building_dict, CreateTourRequestDTO dto, Map<Integer, List<PriceDTO>> pricePerStop) {
        List<Integer> pathBuildingIds = path.stream().map(index -> building_dict.get(stops.get(index))).toList();
        Map<Integer, List<PriceDTO>> filteredPricePerStop = new HashMap<>();

        for (Integer stopId : pathBuildingIds) {
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

    private double getTourDuration(List<Integer> stops, List<List<Float>> durationMatrix){
        double totalDuration = 0;
        for (int i = 0; i < stops.size() - 1; i++) {
            totalDuration += durationMatrix.get(stops.get(i)).get(stops.get(i + 1));
        }
        return totalDuration;
    }


    @ToString
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
            logger.info("Error fetching metric matrix from OpenRouteService: {}", e.getMessage());
            return null;
        }

        try{
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode durations = root.path("durations");
            JsonNode distances = root.path("distances");

            List<List<Float>> distanceArray = mapper.convertValue(distances, new TypeReference<>() {});
            List<List<Float>> durationArray = mapper.convertValue(durations, new TypeReference<>() {});

            switch (metric) {
                case "distance":
                    return distanceArray;
                case "duration":
                    return durationArray;
                default:
                    logger.error("Invalid metric: {}", metric);
            }

        } catch (JsonProcessingException e) {
            logger.error("Could not parse directions response.");
            return null;
        }

        return null;
    }

    private List<Integer> getInterestsFromDB(String userId) {
        return this.userDBClient.getUserInterestsIds(UUID.fromString(userId));
    }

    private Map<Integer, List<PriceDTO>> getPriceFromDB(List<Integer> location_ids) {
        List<PriceDTO> prices = this.userDBClient.getPricesFromDB(location_ids);
        if(prices.isEmpty()) {
         logger.error("Could not retrieve prices from User DB! Either there are no prices available, or there was an issue with the DB connection.");
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
            logger.error("Error accessing OpenRoutingService for distance calculations: {}", response.getBody());
            return null;
        }

        try{
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode summary = root.path("routes").get(0).path("summary");

            double distance = summary.path("distance").asDouble();
            double duration = summary.path("duration").asDouble();

            return Map.of("distance", distance/1000, "duration", duration/3600);

        } catch (JsonProcessingException e) {
            logger.error("Could not parse ORS directions response.");
            return null;
        }

    }
}
