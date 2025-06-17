package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.Entity.Tour.CreateTourRequestDTO;
import group_05.ase.neo4j_data_access.Entity.Tour.TourDTO;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;

import java.util.List;
import java.util.Map;

public interface ITourService {


    List<TourDTO> createTours(CreateTourRequestDTO dto);
    Map<String, Double> getDurationDistanceEstimate(double start_lat, double start_lng, double end_lat, double end_lng, List<ViennaHistoryWikiBuildingObject> stops);
    Map<String, List<List<Float>>> getDurationDistanceMatrix(double start_lat, double start_lng, double end_lat, double end_lng, List<ViennaHistoryWikiBuildingObject> stops);
}
