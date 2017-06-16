package main.java.genetic_algorithm;

import main.java.ProblemModel;

import java.util.Arrays;

/**
 * Created by mmatak on 6/16/17.
 */
public class GA {
    public Chromosome bestSolution;
    // must be even because of start method
    public static int POPULATION_SIZE = 50;
    public static int NUMBER_OF_GENERATIONS = 200;
    public static double CROSSOVER_PROBABILITY = 0.75;
    public static double MUTATION_PROBABILITY = 0.1;
    public Chromosome[] population;

    public GA() {
        population = new Chromosome[POPULATION_SIZE];
    }

    public void start(ProblemModel model) {
        initializePopulation(population);
        EvaluationFunctions.evaluatePopulation(population);
        for(int i = 0; i < NUMBER_OF_GENERATIONS; i++){
            // best solutions are on the lower indexes
            sortPopulation(population);
            Chromosome[] newPopulation = new Chromosome[population.length];
            newPopulation[0] = population[0];
            newPopulation[1] = population[1];
            int currentAvailableIndex = 2;
            for (int j = 0; j < POPULATION_SIZE/2 - 1; j++){
                Chromosome parent1 = Selection.selectParent(population);
                Chromosome parent2 = Selection.selectParent(population);
                Chromosome[] children = Crossover.crossParents(parent1, parent2, CROSSOVER_PROBABILITY);
                Mutation.mutateChild(children[0], MUTATION_PROBABILITY);
                Mutation.mutateChild( children[1], MUTATION_PROBABILITY);
                newPopulation[currentAvailableIndex++] = children[0];
                newPopulation[currentAvailableIndex++] = children[1];
            }
            population = Arrays.copyOf(newPopulation, POPULATION_SIZE);
            EvaluationFunctions.evaluatePopulation(population);
        }
    }
}
