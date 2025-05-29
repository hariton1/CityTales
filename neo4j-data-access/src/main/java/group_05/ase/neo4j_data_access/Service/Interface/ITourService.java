package group_05.ase.neo4j_data_access.Service.Interface;

import group_05.ase.neo4j_data_access.Entity.Tour.TourObject;
import org.springframework.data.neo4j.types.GeographicPoint2d;

import java.util.Optional;

public interface ITourService {


    public TourObject createTour(GeographicPoint2d start, GeographicPoint2d end, String name, Optional<Double> maxDistance, Optional<Double> maxDuration);
    public TourObject createTourBasedOnLocation(GeographicPoint2d location, String name, Optional<Double> maxDistance, Optional<Double> maxDuration);
    public TourObject getTourByName(String name);
    public void deleteTourByName(String name);
}
