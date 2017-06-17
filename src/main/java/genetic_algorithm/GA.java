package main.java.genetic_algorithm;

import main.java.ProblemModel;
import main.java.genetic_algorithm.crossover.Crossover;
import main.java.genetic_algorithm.crossover.StandardCrossover;
import main.java.genetic_algorithm.evaluation.EvaluationFunction;
import main.java.genetic_algorithm.evaluation.StandardEvaluation;
import main.java.genetic_algorithm.mutation.Mutation;
import main.java.genetic_algorithm.mutation.StandardMutation;
import main.java.genetic_algorithm.selection.Selection;
import main.java.genetic_algorithm.selection.TournamentSelection;

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
    public static int K_TOURNAMENT_SELECTION = 3;
    public Chromosome[] population;
    private EvaluationFunction evaluationFunction;
    private Crossover crossover;
    private Mutation mutation;
    private Selection selection;
    private ProblemModel model;


    public GA(ProblemModel model) {
        population = new Chromosome[POPULATION_SIZE];
        evaluationFunction = new StandardEvaluation(model);
        crossover = new StandardCrossover();
        mutation = new StandardMutation();
        selection = new TournamentSelection(K_TOURNAMENT_SELECTION, evaluationFunction);
    }

    public void start() {
        initializePopulation(population);
        evaluationFunction.evaluatePopulation(population);
        for(int i = 0; i < NUMBER_OF_GENERATIONS; i++){
            // best solutions are on the lower indexes
            sortPopulation(population);
            Chromosome[] newPopulation = new Chromosome[population.length];
            newPopulation[0] = population[0];
            newPopulation[1] = population[1];
            int currentAvailableIndex = 2;
            for (int j = 0; j < POPULATION_SIZE/2 - 1; j++){
                Chromosome parent1 = selection.selectParent(population);
                Chromosome parent2 = selection.selectParent(population);
                Chromosome[] children = crossover.crossParents(parent1, parent2, CROSSOVER_PROBABILITY);
                mutation.mutateChild(children[0], MUTATION_PROBABILITY);
                mutation.mutateChild( children[1], MUTATION_PROBABILITY);
                newPopulation[currentAvailableIndex++] = children[0];
                newPopulation[currentAvailableIndex++] = children[1];
            }
            population = Arrays.copyOf(newPopulation, POPULATION_SIZE);
            evaluationFunction.evaluatePopulation(population);
        }
    }

    /**
     * Initialize population to some random values.
     * @param population Array to be instanced with chromosomes.
     */
    private void initializePopulation(Chromosome[] population) {

    }

    /**
     * Sort population by their totalCost.
     * Solutions with less cost after sorting must be on lower indexes.
     * @param population Population to sort.
     */
    private void sortPopulation(Chromosome[] population) {
    }
}
