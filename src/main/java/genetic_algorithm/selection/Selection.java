package main.java.genetic_algorithm.selection;

import main.java.genetic_algorithm.Chromosome;

/**
 * Created by mmatak on 6/16/17.
 */
public abstract class Selection {

    public abstract Chromosome selectParent(Chromosome[] population);
}
