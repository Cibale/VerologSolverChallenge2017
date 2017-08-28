package main.java.genetic_algorithm;

import main.java.ProblemModel;
import main.java.Request;

/**
 * Created by felentovic on 28/08/17.
 */
public abstract class Chromosome {
    public Request[] requests;

    // less is better
    public Long totalCost;
    //cost without punishments
    public Long realCost;
    public ProblemModel model;

    public Chromosome(ProblemModel model){
        this.model = model;
    }

    public abstract int getGenomeValue(int index);

    public abstract void setGenomeValue(int index, int value);

    public abstract  int getChromosomeLength();

    public abstract Chromosome mutateOnIndex(int index);

    public abstract  Long evaluate();

    public abstract void update();

    public abstract Chromosome clone();

    public abstract void initialize();


}
