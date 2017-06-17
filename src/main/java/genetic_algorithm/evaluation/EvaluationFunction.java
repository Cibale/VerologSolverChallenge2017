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
     * Let DTC be cost per unit distance traveled.
     * Let TDTC be total cost for all distance traveled costs.
     * Then TDTC =  sum by each vehicle: sum by each route travelled: distance per route
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
        int tc = calculateTC(chromosome);
        int cost = trvc + tuvc + tdtc + tc;
        chromosome.realCost = cost;
        int punishment = calculatePunishment(chromosome);
        return cost + punishment;
    }

    //TODO

    private int calculateTC(Chromosome chromosome) {
        return 4;
    }

    private int calculateTDTC(Chromosome chromosome) {
        return 3;
    }

    private int calculateTUVC(Chromosome chromosome){
        return 2;
    }

    private int calculateTRVC(Chromosome chromosome) {
        return 1;
    }


    abstract int calculatePunishment(Chromosome chromosome);

    public void evaluatePopulation(Chromosome[] population) {
        for (int i = 0; i < population.length; i++) {
            population[i].totalCost = evaluate(population[i]);
        }
    }
}
