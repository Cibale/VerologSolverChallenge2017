package main.java.genetic_algorithm.evaluation;

import main.java.ProblemModel;
import main.java.genetic_algorithm.Chromosome;

/**
 * Created by mmatak on 6/16/17.
 */
public abstract class EvaluationFunction {
        public ProblemModel model;

        public EvaluationFunction(ProblemModel model){
                this.model = model;
        }
        public abstract Integer evaluate(Chromosome chromosome);
        public void evaluatePopulation(Chromosome[] population){
                for(int i = 0; i < population.length; i++){
                        population[i].totalCost = evaluate(population[i]);
                }
        }
}
