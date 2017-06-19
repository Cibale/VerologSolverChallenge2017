package main.java.genetic_algorithm.evaluation;

import main.java.ProblemModel;
import main.java.genetic_algorithm.Chromosome;

/**
 * Created by mmatak on 6/16/17.
 */
public abstract class EvaluationFunction {
    public ProblemModel model;

    public EvaluationFunction(ProblemModel model) {
        this.model = model;
    }

    /**
     * Total cost is calculated in a way described below.
     * Idea is to have as less cost as possible.
     * <p>
     * Let RVC be cost per "renting" a vehicle,
     * no matter how many time is used as soon as it is more than once).
     * Let TRVC be total "renting" vehicle cost, i.e. total costs related to vehicles.
     * Then TRVC = max{numberOfVehiclesPerDay} * RVC
     * <p>
     * Let UVC be cost of "using" vehicle per day.
     * Let TUVC be total cost of using all the vehicles.
     * Then TUVC = sum by each day in time horizont: numberOfUsedCarsThatDay * UVC
     * <p>
     * Let DTC be cost per unit totalVehicleDistance traveled.
     * Let TDTC be total cost for all totalVehicleDistance traveled costs.
     * Then TDTC =  sum by each vehicle: sum by each route travelled: totalVehicleDistance per route
     * <p>
     * Let TC be total cost for all used tools.
     * Then TC = sum by kinds of tools used: cost of using that kind of tool
     * <p>
     * Let COST denote cost of our solution.
     * Then COST = TRVC + TUVC + TDTC + TC.
     * <p>
     * i.e. total cost of our solution TCOST = COST + PUNISHMENT.
     *
     * @param chromosome representation of solution
     * @return total cost of solution
     */
    public final Integer evaluate(Chromosome chromosome) {
        int trvc = calculateTRVC(chromosome);
        int tuvc = calculateTUVC(chromosome);
        int tdtc = calculateTDTC(chromosome);
        //int tc = calculateTC(chromosome);
        int cost = trvc + tuvc + tdtc;// + tc;
        chromosome.realCost = cost;
        int punishment = calculatePunishment(chromosome);
        chromosome.totalCost = cost + punishment;
        return cost + punishment;
    }

    /**
     * This is always the same. This cost is optimized on the higher level when picking day for delivery request
     * @param chromosome
     * @return
     */
//    private int calculateTC(Chromosome chromosome) {
//        //cost for using tool
//    }

    private int calculateTDTC(Chromosome chromosome) {
        int totalDist = 0;
        for (int i = 0; i < chromosome.vehicles.length; i++) {
            totalDist += chromosome.vehicles[i].totalVehicleDistance;
        }

        return totalDist * model.distanceCost;
    }

    private int calculateTUVC(Chromosome chromosome) {
        //for each vehicle add number of used days
        int totalUsedDays = 0;
        for (int i = 0; i < chromosome.vehicles.length; i++) {
            totalUsedDays += chromosome.vehicles[i].dayRouteMap.size();
        }
        return totalUsedDays * model.vehicleDayCost;
    }

    private int calculateTRVC(Chromosome chromosome) {
        int numOfUsedVehicle = 0;
        for (int i = 0; i < chromosome.vehicles.length; i++) {
            if (chromosome.vehicles[i].usedVehicle()) {
                numOfUsedVehicle++;
            }
        }
        return numOfUsedVehicle * model.vehicleCost;
    }


    abstract int calculatePunishment(Chromosome chromosome);

    public void evaluatePopulation(Chromosome[] population) {
        for (int i = 0; i < population.length; i++) {
            population[i].totalCost = evaluate(population[i]);
        }
    }
}
