package group_05.ase.neo4j_data_access.Entity.Tour;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.driver.Driver;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;

public class CustomMatrixCost implements VehicleRoutingTransportCosts {

    private final double[][] matrix;

    public CustomMatrixCost(double[][] matrix) {
        this.matrix = matrix;
    }

    private int getIndex(String id) {
        return Integer.parseInt(id);
    }

    @Override
    public double getBackwardTransportCost(Location location, Location location1, double v, Driver driver, Vehicle vehicle) {
        return 0;
    }

    @Override
    public double getBackwardTransportTime(Location location, Location location1, double v, Driver driver, Vehicle vehicle) {
        return 0;
    }

    @Override
    public double getTransportCost(Location location, Location location1, double v, Driver driver, Vehicle vehicle) {
        return matrix[getIndex(location.getId())][getIndex(location1.getId())];
    }

    @Override
    public double getTransportTime(Location location, Location location1, double v, Driver driver, Vehicle vehicle) {
        return 0;
    }

    @Override
    public double getDistance(Location location, Location location1, double v, Vehicle vehicle) {
        return 0;
    }
}