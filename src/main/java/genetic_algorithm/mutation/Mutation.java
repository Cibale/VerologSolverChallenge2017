package main.java.genetic_algorithm.mutation;

import main.java.genetic_algorithm.Chromosome;

/**
 * Created by mmatak on 6/16/17.
 */
public abstract class Mutation {

    public abstract void mutateChild(Chromosome child, double MUTATION_PROBABILITY);
}
