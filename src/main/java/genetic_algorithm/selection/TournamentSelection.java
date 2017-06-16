package main.java.genetic_algorithm.selection;

import main.java.genetic_algorithm.Chromosome;
import main.java.genetic_algorithm.EvaluationFunction;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 16.06.17..
 */
public class TournamentSelection extends Selection{
    private int k;
    private EvaluationFunction evaluationFunction;
    public TournamentSelection(int k, EvaluationFunction evaluationFunction){
        this.k = k;
        this.evaluationFunction = evaluationFunction;
    }


    @Override
    public Chromosome selectParent(Chromosome[] population) {
        //find which chromosomes will play tournament
        Integer indices[] = new Integer[k];
        for (int currentIndex = 0; currentIndex < k; currentIndex++){
            while (true) {
                int selected = ThreadLocalRandom.current().nextInt(population.length);
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

        Arrays.sort(indices,(a,b)-> Integer.compare(evaluationFunction.evaluate(population[a]),evaluationFunction.evaluate(population[b])));

        //return the best one
        return population[indices[0]];
    }
}
