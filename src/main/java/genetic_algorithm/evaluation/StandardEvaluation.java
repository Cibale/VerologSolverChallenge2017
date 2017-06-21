package main.java.genetic_algorithm.evaluation;

import main.java.DayRoute;
import main.java.ProblemModel;
import main.java.Vehicle;
import main.java.genetic_algorithm.Chromosome;

/**
 * Created by felentovic on 16.06.17.
 */
public class StandardEvaluation extends EvaluationFunction {


    public StandardEvaluation(ProblemModel model) {
        super(model);
    }


    @Override
    long calculatePunishment(Chromosome chromosome) {
        //punishment for: 1.) exceeded distance traveled
        //                2.) exceeded load capacity
        int totalExceededDistance = 0;
        int totalExceededLoad = 0;
        for (Vehicle vehicle : chromosome.vehicles) {
            if (vehicle.totalVehicleDistance > model.maxTripDistance) {
                totalExceededDistance += vehicle.totalVehicleDistance - model.maxTripDistance;
            }
            for (DayRoute dayRoute : vehicle.dayRouteMap.values()) {
                for (Integer maxRouteLoad : dayRoute.routeMaxLoad) {
                    if (maxRouteLoad > model.capacity) {
                        totalExceededLoad += maxRouteLoad - model.capacity;
                    }
                }
            }
        }
        long costPunishment = 10 * model.vehicleCost;
        // return totalExceededLoad *
        return costPunishment * (totalExceededLoad + totalExceededDistance);
    }


}
