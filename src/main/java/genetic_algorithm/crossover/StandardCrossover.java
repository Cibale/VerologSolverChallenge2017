package main.java.genetic_algorithm.crossover;

import main.java.genetic_algorithm.Chromosome;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Crossover with one point.
 * Created by felentovic on 16.06.17..
 */
public class StandardCrossover extends Crossover {

    @Override
    public Chromosome[] crossParents(Chromosome parent1, Chromosome parent2, double CROSSOVER_PROBABILITY) {
        if (Math.random() > CROSSOVER_PROBABILITY) {
            return new Chromosome[]{parent1.clone(), parent2.clone()};
        }
        int splitPoint = ThreadLocalRandom.current().nextInt(parent1.getChromosomeLength());
        Chromosome child1 = parent1.clone();
        Chromosome child2 = parent2.clone();
        for (int i = 0; i < splitPoint; i++) {
            child1.setGenomeValue(i, parent2.getGenomeValue(i));
            child2.setGenomeValue(i, parent1.getGenomeValue(i));
        }
        child1.update();
        child2.update();

        return new Chromosome[]{child1, child2};
    }
}
