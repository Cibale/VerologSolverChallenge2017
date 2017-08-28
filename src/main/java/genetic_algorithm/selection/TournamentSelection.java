package main.java.genetic_algorithm.selection;

import main.java.genetic_algorithm.Chromosome;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 16.06.17..
 */
public class TournamentSelection extends Selection {
    private int k;

    public TournamentSelection(int k) {
        this.k = k;
    }


    @Override
    public Chromosome selectParent(Chromosome[] population) {
        //find which chromosomes will play tournament
        Integer indices[] = new Integer[k];
        for (int currentIndex = 0; currentIndex < k; currentIndex++) {
            while (true) {
                Integer selected = ThreadLocalRandom.current().nextInt(population.length);
                boolean alreadyIn = false;
                for (int i = 0; i < currentIndex; i++) {
                    if (indices[i] == selected) {
                        alreadyIn = true;
                        break;
                    }
                }
                if (!alreadyIn) {
                    indices[currentIndex] = selected;
                    break;
                }
            }

        }

        Arrays.sort(indices, (a, b) -> population[a].totalCost.compareTo(population[b].totalCost));

        //return the best one
        return population[indices[0]];
    }
}
