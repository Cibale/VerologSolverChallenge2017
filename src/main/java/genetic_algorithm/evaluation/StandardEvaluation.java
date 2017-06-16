package main.java.genetic_algorithm.evaluation;

import main.java.ProblemModel;
import main.java.Vehicle;
import main.java.genetic_algorithm.Chromosome;

/**
 * Created by felentovic on 16.06.17..
 */
public class StandardEvaluation extends EvaluationFunction {


    public StandardEvaluation(ProblemModel model) {
        super(model);
    }

    @Override
    public Integer evaluate(Chromosome chromosome) {
        //vehicle a day cost
        //distance cost
        int cost = 0;
        for (int i = 0; i < chromosome.vehicles.length; i++){
            Vehicle vehicle = chromosome.vehicles[i];
                if (!vehicle.usedVehicle()){
                    continue;
                }
            cost += this.model.vehicleCost;
            cost += vehicle.distance * model.distanceCost;
            //add cost if solution is not feasible
        }

        return cost;
    }
}
