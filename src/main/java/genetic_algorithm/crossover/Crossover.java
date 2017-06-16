package main.java.genetic_algorithm.crossover;

import main.java.genetic_algorithm.Chromosome;

/**
 * Created by mmatak on 6/16/17.
 */
public abstract class Crossover {

    public abstract Chromosome[] crossParents(Chromosome parent1, Chromosome parent2, double CROSSOVER_PROBABILITY);
}
