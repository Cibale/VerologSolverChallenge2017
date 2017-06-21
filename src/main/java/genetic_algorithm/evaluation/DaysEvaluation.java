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

    public DaysEvaluation(ProblemModel model) {
        this.model = model;
    }

    public Long evaluate(DaysChromosome chromosome) {
        long punishment = 0;
        chromosome.sumExceeded = 0;
        chromosome.exceededToolIdSet.clear();
        int[] toolsUsed = new int[model.tools.length];
        for (Map.Entry<Integer, Map<Integer, DaysChromosome.UsageToolRequests>> day_ToolUsedRequests : chromosome.daysMap.entrySet()) {
            for (Map.Entry<Integer, DaysChromosome.UsageToolRequests> toolId_UsageToolClass : day_ToolUsedRequests.getValue().entrySet()) {
                Tool tool = model.tools[toolId_UsageToolClass.getKey()];
                if(toolsUsed[tool.id] < toolId_UsageToolClass.getValue().usage ){
                    toolsUsed[tool.id] = toolId_UsageToolClass.getValue().usage;
                }
                //punishment
                int exceeded = (toolId_UsageToolClass.getValue().usage - tool.availableNum);
                if (exceeded > 0) {
                    punishment += exceeded ;//* tool.cost;
                    boolean bol = chromosome.exceededToolIdSet.add(chromosome.new ExceededToolId(exceeded, tool.id, day_ToolUsedRequests.getKey()));
                     chromosome.exceededToolIdSet.add(chromosome.new ExceededToolId(exceeded, tool.id, day_ToolUsedRequests.getKey()));
                    chromosome.sumExceeded+=exceeded;
                }
            }
        }
        long cost = 0;
        for(int i = 1; i < toolsUsed.length; i++){
            cost+=toolsUsed[i] * model.tools[i].cost;
        }
        chromosome.realCost = cost;
        chromosome.totalCost = cost + punishment;
        chromosome.toolUsed = toolsUsed;
        return chromosome.totalCost;
    }

    public void evaluatePopulation(DaysChromosome[] population) {
        for (int i = 0; i < population.length; i++) {
            population[i].totalCost = evaluate(population[i]);
        }
    }
}
