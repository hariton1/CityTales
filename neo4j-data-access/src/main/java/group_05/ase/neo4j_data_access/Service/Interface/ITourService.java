package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.Entity.Tour.TourObject;
import group_05.ase.neo4j_data_access.Entity.ViennaHistoryWikiBuildingObject;
import org.springframework.data.neo4j.types.GeographicPoint2d;

import java.util.List;
import java.util.Map;

public interface ITourService {


    public TourObject createTour(String name, String description, double start_lat, double start_lng, double end_lat, double end_lng, List<ViennaHistoryWikiBuildingObject> stops, String userId);
    public Map<String, Double> getDurationDistanceEstimate(double start_lat, double start_lng, double end_lat, double end_lng, List<ViennaHistoryWikiBuildingObject> stops);
    public TourObject getTourByName(String name);
    public void deleteTourByName(String name);
}
