package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.Entity.Tour.TourObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;

import java.util.List;
import java.util.Map;

public interface ITourService {


    TourObject createTour(String name, String description, double start_lat, double start_lng, double end_lat, double end_lng, List<ViennaHistoryWikiBuildingObject> stops, String userId);
    Map<String, Double> getDurationDistanceEstimate(double start_lat, double start_lng, double end_lat, double end_lng, List<ViennaHistoryWikiBuildingObject> stops);
    TourObject getTourByName(String name);
    void deleteTourByName(String name);
}
