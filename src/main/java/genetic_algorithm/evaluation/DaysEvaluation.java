package main.java.genetic_algorithm.evaluation;

import main.java.ProblemModel;
import main.java.Tool;
import main.java.genetic_algorithm.DaysChromosome;

import java.util.Map;

/**
 * Created by felentovic on 20.06.17..
 */
public class DaysEvaluation {
    private ProblemModel model;

    public DaysEvaluation(ProblemModel model){
        this.model = model;
    }

    public Integer evaluate(DaysChromosome chromosome){
        int cost = 0;
        int punishment = 0;
        for (Map<Integer, Integer> toolUsed : chromosome.days.values()){
            for(Map.Entry<Integer,Integer> entry : toolUsed.entrySet()){
                Tool tool = model.tools[entry.getKey()];
                cost += tool.cost * entry.getValue();
                //punishment
                if (entry.getValue() > tool.availableNum){
                    punishment += (entry.getValue() - tool.availableNum) * model.vehicleCost;
                }
            }
        }
        chromosome.realCost = cost;
        chromosome.totalCost = cost + punishment;
        return  chromosome.totalCost;
    }

    public void evaluatePopulation(DaysChromosome[] population) {
        for (int i = 0; i < population.length; i++) {
            population[i].totalCost = evaluate(population[i]);
        }
    }
}
