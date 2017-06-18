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
    int calculatePunishment(Chromosome chromosome) {
        //punishment for: 1.) exceeded distance traveled
        //                2.) exceeded load capacity
        int totalExceedeDistance = 0;
        int totalExceededLoad = 0;
        for (Vehicle vehicle : chromosome.vehicles){
                if (vehicle.totalVehicleDistance > model.maxTripDistance){
                    totalExceedeDistance += vehicle.totalVehicleDistance - model.maxTripDistance;
                }
                for (DayRoute dayRoute : vehicle.dayRouteMap.values()){
                    for (Integer maxRouteLoad : dayRoute.oldRouteMaxLoads){
                        if(maxRouteLoad > model.capacity){
                            totalExceededLoad += maxRouteLoad - model.capacity;
                        }
                    }
                }
        }
        return totalExceededLoad * model.capacity + totalExceedeDistance * model.maxTripDistance;
    }


}
