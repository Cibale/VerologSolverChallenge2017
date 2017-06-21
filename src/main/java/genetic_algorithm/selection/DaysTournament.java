package main.java.genetic_algorithm.selection;

import main.java.genetic_algorithm.DaysChromosome;
import main.java.genetic_algorithm.evaluation.DaysEvaluation;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 20.06.17..
 */
public class DaysTournament {
    private int k;
    private DaysEvaluation evaluationFunction;
    public DaysTournament(int k, DaysEvaluation evaluationFunction){
        this.k = k;
        this.evaluationFunction = evaluationFunction;
    }


    public DaysChromosome selectParent(DaysChromosome[] population) {
        //find which chromosomes will play tournament
        Integer indices[] = new Integer[k];
        for (int currentIndex = 0; currentIndex < k; currentIndex++){
            while (true) {
                Integer selected = ThreadLocalRandom.current().nextInt(population.length);
                boolean alreadyIn = false;
                for(int i=0; i < currentIndex; i++){
                    if (indices[i] == selected){
                        alreadyIn = true;
                        break;
                    }
                }
                if (!alreadyIn){
                    indices[currentIndex] = selected;
                    break;
                }
            }

        }

        Arrays.sort(indices,(a, b)-> evaluationFunction.evaluate(population[a]).compareTo(evaluationFunction.evaluate(population[b])));

        //return the best one
        return population[indices[0]];
    }
}
