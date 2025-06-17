package group_05.ase.neo4j_data_access.Services;
import group_05.ase.neo4j_data_access.Client.QdrantClient;
import group_05.ase.neo4j_data_access.Client.UserDBClient;
import group_05.ase.neo4j_data_access.Entity.Tour.CreateTourRequestDTO;
import group_05.ase.neo4j_data_access.Entity.Tour.PriceDTO;
import group_05.ase.neo4j_data_access.Entity.Tour.TourDTO;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import group_05.ase.neo4j_data_access.Service.Implementation.HistoricBuildingService;
import group_05.ase.neo4j_data_access.Service.Implementation.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    // ───────────────────── mocked collaborators ─────────────────────
    @Mock
    HistoricBuildingService historicBuildingService;
    @Mock
    UserDBClient userDBClient;
    @Mock
    QdrantClient qdrantClient;

    // service under test (created in @BeforeEach so we can spy later)
    TourService tourService;

    // common test fixtures
    static final UUID USER_ID       = UUID.randomUUID();
    static final int   BUILDING_1_ID = 1001;
    static final int   BUILDING_2_ID = 1002;
    static final double START_LAT = 48.208354;
    static final double START_LNG = 16.372504;
    static final double END_LAT = 48.208354;
    static final double END_LNG = 16.372504;

    // distance matrix used in both branches (values are metres)
    static final String MATRIX_JSON = """
{
  "distances": [
    [0, 10, 20, 30],
    [10, 0, 15, 25],
    [20, 15, 0, 18],
    [30, 25, 18, 0]
  ],
  "durations": [
    [0, 8, 16, 24],
    [8, 0, 12, 20],
    [16, 12, 0, 14],
    [24, 20, 14, 0]
  ]
}
""";

    static final String DIRECTIONS_JSON = """
{
  "routes": [
    {
      "summary": {
        "distance": 450.0,
        "duration": 1234
      }
    }
  ]
}
""";


    private ViennaHistoryWikiBuildingObject mockBuilding(int id, double lat, double lng) {
        ViennaHistoryWikiBuildingObject obj = new ViennaHistoryWikiBuildingObject();
        obj.setViennaHistoryWikiId(id);
        obj.setLatitude(Optional.of(lat));
        obj.setLongitude(Optional.of(lng));
        obj.setName("Building-" + id);
        return obj;
    }

    private CreateTourRequestDTO baseRequest_twoStops() {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setUserId(USER_ID.toString());
        dto.setStart_lat(START_LAT);
        dto.setStart_lng(START_LNG);
        dto.setEnd_lat(END_LAT);                     // no explicit end in these tests
        dto.setEnd_lng(END_LNG);
        dto.setNumStops(2);
        dto.setMinDistance(0);
        dto.setMaxDistance(200000);             // 2 km
        dto.setMaxBudget(100);
        dto.setPersonConfiguration(new int[]{1,0,0});
        dto.setPredefinedStops(List.of());     // none
        return dto;
    }

    private CreateTourRequestDTO baseRequest_zeroStops() {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setUserId(USER_ID.toString());
        dto.setStart_lat(START_LAT);
        dto.setStart_lng(START_LNG);
        dto.setEnd_lat(END_LAT);                     // no explicit end in these tests
        dto.setEnd_lng(END_LNG);
        dto.setNumStops(0);
        dto.setMinDistance(0);
        dto.setMaxDistance(200000);             // 2 km
        dto.setMaxBudget(100);
        dto.setPersonConfiguration(new int[]{1,0,0});
        dto.setPredefinedStops(List.of());     // none
        return dto;
    }

    private CreateTourRequestDTO baseRequest_wholeFamily() {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setUserId(USER_ID.toString());
        dto.setStart_lat(START_LAT);
        dto.setStart_lng(START_LNG);
        dto.setEnd_lat(END_LAT);                     // no explicit end in these tests
        dto.setEnd_lng(END_LNG);
        dto.setNumStops(1);
        dto.setMinDistance(0);
        dto.setMaxDistance(200000);             // 2 km
        dto.setMaxBudget(200);
        dto.setPersonConfiguration(new int[]{2,2,2});
        dto.setPredefinedStops(List.of());     // none
        return dto;
    }

    private CreateTourRequestDTO baseRequest_impossibleDistance() {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setUserId(USER_ID.toString());
        dto.setStart_lat(START_LAT);
        dto.setStart_lng(START_LNG);
        dto.setEnd_lat(END_LAT);                     // no explicit end in these tests
        dto.setEnd_lng(END_LNG);
        dto.setNumStops(0);
        dto.setMinDistance(0);
        dto.setMaxDistance(0);             // 2 km
        dto.setMaxBudget(100);
        dto.setPersonConfiguration(new int[]{1,0,0});
        dto.setPredefinedStops(List.of());     // none
        return dto;
    }

    private CreateTourRequestDTO baseRequest_BudgetConstraintWorks() {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setUserId(USER_ID.toString());
        dto.setStart_lat(START_LAT);
        dto.setStart_lng(START_LNG);
        dto.setEnd_lat(END_LAT);                     // no explicit end in these tests
        dto.setEnd_lng(END_LNG);
        dto.setNumStops(1);
        dto.setMinDistance(0);
        dto.setMaxDistance(200000);             // 2 km
        dto.setMaxBudget(99);
        dto.setPersonConfiguration(new int[]{2,2,2});
        dto.setPredefinedStops(List.of());     // none
        return dto;
    }

    private CreateTourRequestDTO baseRequest_DistanceConstraintWorks() {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setUserId(USER_ID.toString());
        dto.setStart_lat(START_LAT);
        dto.setStart_lng(START_LNG);
        dto.setEnd_lat(END_LAT);                     // no explicit end in these tests
        dto.setEnd_lng(END_LNG);
        dto.setNumStops(1);
        dto.setMinDistance(0);
        dto.setMaxDistance(35);             // 2 km
        dto.setMaxBudget(100);
        dto.setPersonConfiguration(new int[]{0,0,0});
        dto.setPredefinedStops(List.of());     // none
        return dto;
    }

    private CreateTourRequestDTO baseRequest_PredefinedStops() {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setUserId(USER_ID.toString());
        dto.setStart_lat(START_LAT);
        dto.setStart_lng(START_LNG);
        dto.setEnd_lat(END_LAT);                     // no explicit end in these tests
        dto.setEnd_lng(END_LNG);
        dto.setNumStops(1);
        dto.setMinDistance(0);
        dto.setMaxDistance(200000);             // 2 km
        dto.setMaxBudget(1000000);
        dto.setPersonConfiguration(new int[]{0,0,0});
        dto.setPredefinedStops(List.of(mockBuilding(BUILDING_2_ID, 48.2070, 16.3725)));     // none
        return dto;
    }

    private CreateTourRequestDTO baseRequest_missingStartOrEndCoordinates() {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setUserId(USER_ID.toString());
        dto.setStart_lat(0.0);
        dto.setStart_lng(0.0);
        dto.setEnd_lat(0.0);                     // no explicit end in these tests
        dto.setEnd_lng(0.0);
        dto.setNumStops(1);
        dto.setMinDistance(0);
        dto.setMaxDistance(200000);             // 2 km
        dto.setMaxBudget(1000000);
        dto.setPersonConfiguration(new int[]{0,0,0});
        dto.setPredefinedStops(List.of());     // none
        return dto;
    }

    @BeforeEach
    void setUp() {
        tourService = spy(new TourService(historicBuildingService, userDBClient, qdrantClient));
    }

    @Test
    void createTours_bfsBranch_return_two() throws Exception {

        when(userDBClient.getUserInterestsIds(USER_ID)).thenReturn(List.of(1, 2));
        when(qdrantClient.getBuildingEntityIdsFromQdrant(any())).thenReturn(List.of(BUILDING_1_ID, BUILDING_2_ID));
        when(historicBuildingService.getBuildingById(BUILDING_1_ID))
                .thenReturn(mockBuilding(BUILDING_1_ID, 48.2060, 16.3705));
        when(historicBuildingService.getBuildingById(BUILDING_2_ID))
                .thenReturn(mockBuilding(BUILDING_2_ID, 48.2070, 16.3725)); // ~150 m away

        PriceDTO price = new PriceDTO(0, BUILDING_1_ID, 20.0f, "Adult", "", LocalDateTime.now());
        PriceDTO price2 = new PriceDTO(0, BUILDING_2_ID, 10.0f, "Adult", "", LocalDateTime.now());
        when(userDBClient.getPricesFromDB(anyList())).thenReturn(List.of(price, price2));


        try (MockedConstruction<RestTemplate> mockedRest = Mockito.mockConstruction(
                RestTemplate.class,
                (mock, ctx) -> {
                    when(mock.postForEntity(anyString(), any(), eq(String.class)))
                            .thenAnswer(invocation -> {
                                String url = invocation.getArgument(0, String.class);

                                if (url.contains("/matrix")) {
                                    return new ResponseEntity<>(MATRIX_JSON, HttpStatus.OK);
                                } else if (url.contains("/directions")) {
                                    return new ResponseEntity<>(DIRECTIONS_JSON, HttpStatus.OK);
                                } else {
                                    throw new IllegalArgumentException("Unexpected endpoint: " + url);
                                }
                            });
                })) {


            List<TourDTO> tours = tourService.createTours(baseRequest_twoStops());

            assertThat(tours).hasSize(2);
            TourDTO tour = tours.get(0);
            assertThat(tour.getDistance()).isBetween(0.0, 0.06); //km
            assertThat(tour.getDurationEstimate()).isBetween(0.0, 2.0); //h
            assertThat(tour.getTourPrice()).isBetween(0.0, 30.0);
            verify(tourService, never()).findRoutesGraphhopper(any(List.class), anyInt(), anyDouble(), anyDouble(), any(List.class), any(Map.class), any(CreateTourRequestDTO.class), any(Map.class));          // Graphhopper never used
        }
    }

    @Test
    void createTours_bfsBranch_return_one() throws Exception {

        when(userDBClient.getUserInterestsIds(USER_ID)).thenReturn(List.of(1, 2));
        when(qdrantClient.getBuildingEntityIdsFromQdrant(any())).thenReturn(List.of(BUILDING_1_ID, BUILDING_2_ID));
        when(historicBuildingService.getBuildingById(BUILDING_1_ID))
                .thenReturn(mockBuilding(BUILDING_1_ID, 48.2060, 16.3705));
        when(historicBuildingService.getBuildingById(BUILDING_2_ID))
                .thenReturn(mockBuilding(BUILDING_2_ID, 48.2070, 16.3725)); // ~150 m away

        PriceDTO price = new PriceDTO(0, BUILDING_1_ID, 20.0f, "Adult", "", LocalDateTime.now());
        PriceDTO price2 = new PriceDTO(0, BUILDING_2_ID, 10.0f, "Adult", "", LocalDateTime.now());
        when(userDBClient.getPricesFromDB(anyList())).thenReturn(List.of(price, price2));


        try (MockedConstruction<RestTemplate> mockedRest = Mockito.mockConstruction(
                RestTemplate.class,
                (mock, ctx) -> {
                    when(mock.postForEntity(anyString(), any(), eq(String.class)))
                            .thenAnswer(invocation -> {
                                String url = invocation.getArgument(0, String.class);

                                if (url.contains("/matrix")) {
                                    return new ResponseEntity<>(MATRIX_JSON, HttpStatus.OK);
                                } else if (url.contains("/directions")) {
                                    return new ResponseEntity<>(DIRECTIONS_JSON, HttpStatus.OK);
                                } else {
                                    throw new IllegalArgumentException("Unexpected endpoint: " + url);
                                }
                            });
                })) {


            List<TourDTO> tours = tourService.createTours(baseRequest_zeroStops());
            assertThat(tours).hasSize(1);
            TourDTO tour = tours.get(0);
            assertThat(tour.getDistance()).isEqualTo(0.03); // km
            assertThat(tour.getDurationEstimate()).isBetween(0.0, 0.1); //h
            assertThat(tour.getTourPrice()).isEqualTo(0.0);
            verify(tourService, never()).findRoutesGraphhopper(any(List.class), anyInt(), anyDouble(), anyDouble(), any(List.class), any(Map.class), any(CreateTourRequestDTO.class), any(Map.class));          }
    }

    @Test
    void createTours_bfsBranch_return_none_invoke_graphhopper() throws Exception {

        when(userDBClient.getUserInterestsIds(USER_ID)).thenReturn(List.of(1, 2));
        when(qdrantClient.getBuildingEntityIdsFromQdrant(any())).thenReturn(List.of(BUILDING_1_ID, BUILDING_2_ID));
        when(historicBuildingService.getBuildingById(BUILDING_1_ID))
                .thenReturn(mockBuilding(BUILDING_1_ID, 48.2060, 16.3705));
        when(historicBuildingService.getBuildingById(BUILDING_2_ID))
                .thenReturn(mockBuilding(BUILDING_2_ID, 48.2070, 16.3725)); // ~150 m away


        try (MockedConstruction<RestTemplate> mockedRest = Mockito.mockConstruction(
                RestTemplate.class,
                (mock, ctx) -> {
                    when(mock.postForEntity(anyString(), any(), eq(String.class)))
                            .thenAnswer(invocation -> {
                                String url = invocation.getArgument(0, String.class);

                                if (url.contains("/matrix")) {
                                    return new ResponseEntity<>(MATRIX_JSON, HttpStatus.OK);
                                } else if (url.contains("/directions")) {
                                    return new ResponseEntity<>(DIRECTIONS_JSON, HttpStatus.OK);
                                } else {
                                    throw new IllegalArgumentException("Unexpected endpoint: " + url);
                                }
                            });
                })) {


            List<TourDTO> tours = tourService.createTours(baseRequest_impossibleDistance());

            assertThat(tours).hasSize(0);
            verify(tourService, atLeastOnce()).findRoutesGraphhopper(any(List.class), anyInt(), anyDouble(), anyDouble(), any(List.class), any(Map.class), any(CreateTourRequestDTO.class), any(Map.class));          }
    }

    @Test
    void createTours_testPriceCalculation() throws Exception {

        when(userDBClient.getUserInterestsIds(USER_ID)).thenReturn(List.of(1, 2));
        when(qdrantClient.getBuildingEntityIdsFromQdrant(any())).thenReturn(List.of(BUILDING_1_ID, BUILDING_2_ID));
        when(historicBuildingService.getBuildingById(BUILDING_1_ID))
                .thenReturn(mockBuilding(BUILDING_1_ID, 48.2060, 16.3705));
        when(historicBuildingService.getBuildingById(BUILDING_2_ID))
                .thenReturn(mockBuilding(BUILDING_2_ID, 48.2070, 16.3725)); // ~150 m away

        PriceDTO price = new PriceDTO(0, BUILDING_1_ID, 20.0f, "Adult", "", LocalDateTime.now());
        PriceDTO price2 = new PriceDTO(0, BUILDING_2_ID, 10.0f, "Adult", "", LocalDateTime.now());
        PriceDTO price3 = new PriceDTO(0, BUILDING_1_ID, 5.0f, "Child", "", LocalDateTime.now());
        PriceDTO price4 = new PriceDTO(0, BUILDING_2_ID, 7.0f, "Child", "", LocalDateTime.now());
        PriceDTO price5 = new PriceDTO(0, BUILDING_1_ID, 25.0f, "Senior", "", LocalDateTime.now());
        PriceDTO price6 = new PriceDTO(0, BUILDING_2_ID, 21.0f, "Senior", "", LocalDateTime.now());

        when(userDBClient.getPricesFromDB(anyList())).thenReturn(List.of(price, price2, price3, price4, price5, price6));


        try (MockedConstruction<RestTemplate> mockedRest = Mockito.mockConstruction(
                RestTemplate.class,
                (mock, ctx) -> {
                    when(mock.postForEntity(anyString(), any(), eq(String.class)))
                            .thenAnswer(invocation -> {
                                String url = invocation.getArgument(0, String.class);

                                if (url.contains("/matrix")) {
                                    return new ResponseEntity<>(MATRIX_JSON, HttpStatus.OK);
                                } else if (url.contains("/directions")) {
                                    return new ResponseEntity<>(DIRECTIONS_JSON, HttpStatus.OK);
                                } else {
                                    throw new IllegalArgumentException("Unexpected endpoint: " + url);
                                }
                            });
                })) {


            List<TourDTO> tours = tourService.createTours(baseRequest_wholeFamily());

            assertThat(tours).hasSize(2);
            TourDTO tour = tours.get(0);
            if(tour.getStops().contains("1001")){
                assertThat(tour.getTourPrice()).isEqualTo(100.0);
                assertThat(tour.getPricePerStop()).contains("20.0").contains("5.0").contains("25.0");
                assertThat(tour.getStops()).doesNotContain("1002");
            }
            if(tour.getStops().contains("1002")){
                assertThat(tour.getTourPrice()).isEqualTo(76.0);
                assertThat(tour.getPricePerStop()).contains("10.0").contains("7.0").contains("21.0");
                assertThat(tour.getStops()).doesNotContain("1001");
            }

            verify(tourService, never()).findRoutesGraphhopper(any(List.class), anyInt(), anyDouble(), anyDouble(), any(List.class), any(Map.class), any(CreateTourRequestDTO.class), any(Map.class));          }
    }

    @Test
    void createTours_testBudgetConstraint() throws Exception {

        when(userDBClient.getUserInterestsIds(USER_ID)).thenReturn(List.of(1, 2));
        when(qdrantClient.getBuildingEntityIdsFromQdrant(any())).thenReturn(List.of(BUILDING_1_ID, BUILDING_2_ID));
        when(historicBuildingService.getBuildingById(BUILDING_1_ID))
                .thenReturn(mockBuilding(BUILDING_1_ID, 48.2060, 16.3705));
        when(historicBuildingService.getBuildingById(BUILDING_2_ID))
                .thenReturn(mockBuilding(BUILDING_2_ID, 48.2070, 16.3725)); // ~150 m away

        PriceDTO price = new PriceDTO(0, BUILDING_1_ID, 20.0f, "Adult", "", LocalDateTime.now());
        PriceDTO price2 = new PriceDTO(0, BUILDING_2_ID, 10.0f, "Adult", "", LocalDateTime.now());
        PriceDTO price3 = new PriceDTO(0, BUILDING_1_ID, 5.0f, "Child", "", LocalDateTime.now());
        PriceDTO price4 = new PriceDTO(0, BUILDING_2_ID, 7.0f, "Child", "", LocalDateTime.now());
        PriceDTO price5 = new PriceDTO(0, BUILDING_1_ID, 25.0f, "Senior", "", LocalDateTime.now());
        PriceDTO price6 = new PriceDTO(0, BUILDING_2_ID, 21.0f, "Senior", "", LocalDateTime.now());

        when(userDBClient.getPricesFromDB(anyList())).thenReturn(List.of(price, price2, price3, price4, price5, price6));


        try (MockedConstruction<RestTemplate> mockedRest = Mockito.mockConstruction(
                RestTemplate.class,
                (mock, ctx) -> {
                    when(mock.postForEntity(anyString(), any(), eq(String.class)))
                            .thenAnswer(invocation -> {
                                String url = invocation.getArgument(0, String.class);

                                if (url.contains("/matrix")) {
                                    return new ResponseEntity<>(MATRIX_JSON, HttpStatus.OK);
                                } else if (url.contains("/directions")) {
                                    return new ResponseEntity<>(DIRECTIONS_JSON, HttpStatus.OK);
                                } else {
                                    throw new IllegalArgumentException("Unexpected endpoint: " + url);
                                }
                            });
                })) {


            List<TourDTO> tours = tourService.createTours(baseRequest_BudgetConstraintWorks());

            assertThat(tours).hasSize(1);
            TourDTO tour = tours.get(0);
            assertThat(tour.getTourPrice()).isLessThan(100.0);

            verify(tourService, never()).findRoutesGraphhopper(any(List.class), anyInt(), anyDouble(), anyDouble(), any(List.class), any(Map.class), any(CreateTourRequestDTO.class), any(Map.class));          }
    }

    @Test
    void createTours_testDistanceConstraint() throws Exception {

        when(userDBClient.getUserInterestsIds(USER_ID)).thenReturn(List.of(1, 2));
        when(qdrantClient.getBuildingEntityIdsFromQdrant(any())).thenReturn(List.of(BUILDING_1_ID, BUILDING_2_ID));
        when(historicBuildingService.getBuildingById(BUILDING_1_ID))
                .thenReturn(mockBuilding(BUILDING_1_ID, 48.2060, 16.3705));
        when(historicBuildingService.getBuildingById(BUILDING_2_ID))
                .thenReturn(mockBuilding(BUILDING_2_ID, 48.2070, 16.3725)); // ~150 m away

        PriceDTO price = new PriceDTO(0, BUILDING_1_ID, 20.0f, "Adult", "", LocalDateTime.now());
        PriceDTO price2 = new PriceDTO(0, BUILDING_2_ID, 10.0f, "Adult", "", LocalDateTime.now());
        PriceDTO price3 = new PriceDTO(0, BUILDING_1_ID, 5.0f, "Child", "", LocalDateTime.now());
        PriceDTO price4 = new PriceDTO(0, BUILDING_2_ID, 7.0f, "Child", "", LocalDateTime.now());
        PriceDTO price5 = new PriceDTO(0, BUILDING_1_ID, 25.0f, "Senior", "", LocalDateTime.now());
        PriceDTO price6 = new PriceDTO(0, BUILDING_2_ID, 21.0f, "Senior", "", LocalDateTime.now());

        when(userDBClient.getPricesFromDB(anyList())).thenReturn(List.of(price, price2, price3, price4, price5, price6));


        try (MockedConstruction<RestTemplate> mockedRest = Mockito.mockConstruction(
                RestTemplate.class,
                (mock, ctx) -> {
                    when(mock.postForEntity(anyString(), any(), eq(String.class)))
                            .thenAnswer(invocation -> {
                                String url = invocation.getArgument(0, String.class);

                                if (url.contains("/matrix")) {
                                    return new ResponseEntity<>(MATRIX_JSON, HttpStatus.OK);
                                } else if (url.contains("/directions")) {
                                    return new ResponseEntity<>(DIRECTIONS_JSON, HttpStatus.OK);
                                } else {
                                    throw new IllegalArgumentException("Unexpected endpoint: " + url);
                                }
                            });
                })) {


            List<TourDTO> tours = tourService.createTours(baseRequest_DistanceConstraintWorks());

            assertThat(tours).hasSize(1);
            TourDTO tour = tours.get(0);
            assertThat(tour.getDistance()).isLessThanOrEqualTo(35.0);

            verify(tourService, never()).findRoutesGraphhopper(any(List.class), anyInt(), anyDouble(), anyDouble(), any(List.class), any(Map.class), any(CreateTourRequestDTO.class), any(Map.class));          }
    }

    @Test
    void createTours_testContainsPredefinedStops() throws Exception {

        when(userDBClient.getUserInterestsIds(USER_ID)).thenReturn(List.of(1, 2));
        when(qdrantClient.getBuildingEntityIdsFromQdrant(any())).thenReturn(List.of(BUILDING_1_ID, BUILDING_2_ID));
        when(historicBuildingService.getBuildingById(BUILDING_1_ID))
                .thenReturn(mockBuilding(BUILDING_1_ID, 48.2060, 16.3705));
        when(historicBuildingService.getBuildingById(BUILDING_2_ID))
                .thenReturn(mockBuilding(BUILDING_2_ID, 48.2070, 16.3725)); // ~150 m away

        PriceDTO price = new PriceDTO(0, BUILDING_1_ID, 20.0f, "Adult", "", LocalDateTime.now());
        PriceDTO price2 = new PriceDTO(0, BUILDING_2_ID, 10.0f, "Adult", "", LocalDateTime.now());
        PriceDTO price3 = new PriceDTO(0, BUILDING_1_ID, 5.0f, "Child", "", LocalDateTime.now());
        PriceDTO price4 = new PriceDTO(0, BUILDING_2_ID, 7.0f, "Child", "", LocalDateTime.now());
        PriceDTO price5 = new PriceDTO(0, BUILDING_1_ID, 25.0f, "Senior", "", LocalDateTime.now());
        PriceDTO price6 = new PriceDTO(0, BUILDING_2_ID, 21.0f, "Senior", "", LocalDateTime.now());

        when(userDBClient.getPricesFromDB(anyList())).thenReturn(List.of(price, price2, price3, price4, price5, price6));


        try (MockedConstruction<RestTemplate> mockedRest = Mockito.mockConstruction(
                RestTemplate.class,
                (mock, ctx) -> {
                    when(mock.postForEntity(anyString(), any(), eq(String.class)))
                            .thenAnswer(invocation -> {
                                String url = invocation.getArgument(0, String.class);

                                if (url.contains("/matrix")) {
                                    return new ResponseEntity<>(MATRIX_JSON, HttpStatus.OK);
                                } else if (url.contains("/directions")) {
                                    return new ResponseEntity<>(DIRECTIONS_JSON, HttpStatus.OK);
                                } else {
                                    throw new IllegalArgumentException("Unexpected endpoint: " + url);
                                }
                            });
                })) {


            List<TourDTO> tours = tourService.createTours(baseRequest_PredefinedStops());

            assertThat(tours).hasSize(1);
            TourDTO tour = tours.get(0);
            assertThat(tour.getStops()).contains("1002");
            assertThat(tour.getStops()).doesNotContain("1001");

            verify(tourService, never()).findRoutesGraphhopper(any(List.class), anyInt(), anyDouble(), anyDouble(), any(List.class), any(Map.class), any(CreateTourRequestDTO.class), any(Map.class));          }
    }

    @Test
    void createTours_testMissingStartEndPoint() throws Exception {
        List<TourDTO> tours = tourService.createTours(baseRequest_missingStartOrEndCoordinates());

        assertThat(tours).hasSize(0);

        verify(tourService, never()).findToursBFS(any(List.class), any(List.class), anyDouble(), anyInt(), any(Set.class), anyInt(), anyDouble(), any(CreateTourRequestDTO.class), any(Map.class), any(List.class), any(Map.class));
        verify(tourService, never()).findRoutesGraphhopper(any(List.class), anyInt(), anyDouble(), anyDouble(), any(List.class), any(Map.class), any(CreateTourRequestDTO.class), any(Map.class));      }
}
