package main.java.genetic_algorithm;

import main.java.ProblemModel;
import main.java.genetic_algorithm.crossover.Crossover;
import main.java.genetic_algorithm.crossover.StandardCrossover;
import main.java.genetic_algorithm.mutation.Mutation;
import main.java.genetic_algorithm.mutation.StandardMutation;
import main.java.genetic_algorithm.selection.Selection;
import main.java.genetic_algorithm.selection.TournamentSelection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

/**
 * Created by mmatak on 6/16/17.
 */
public class GA {
    public Chromosome bestSolution;
    // must be even because of start method
    public int POPULATION_SIZE;
    public int NUMBER_OF_GENERATIONS;
    public double CROSSOVER_PROBABILITY;
    public double MUTATION_PROBABILITY;
    public int K_TOURNAMENT_SELECTION;
    public Chromosome[] population;
    private Crossover crossover;
    private Mutation mutation;
    private Selection selection;
    private ProblemModel model;
    private Supplier<Chromosome> factory;

    public GA(ProblemModel model, Supplier<Chromosome> factory, int POPULATION_SIZE, int k_TOURNAMENT_SELECTION) {
        this.model = model;
        this.factory = factory;
        this.K_TOURNAMENT_SELECTION = k_TOURNAMENT_SELECTION;
        this.POPULATION_SIZE = POPULATION_SIZE;
        population = new Chromosome[POPULATION_SIZE];
        crossover = new StandardCrossover();
        mutation = new StandardMutation();
        selection = new TournamentSelection(K_TOURNAMENT_SELECTION);
    }

    public void start() {
        initializePopulation(population);
        evaluatePopulation(population);
        sortPopulation(population);
        Chromosome[] newPopulation = new Chromosome[population.length];

        for (int i = 0; i <= NUMBER_OF_GENERATIONS; i++) {
            // best solutions are on the lower indexes
            newPopulation[0] = population[0];
            newPopulation[1] = population[1];
            int currentAvailableIndex = 2;
            for (int j = 0; j < POPULATION_SIZE / 2 - 1; j++) {
                Chromosome parent1 = selection.selectParent(population);
                Chromosome parent2 = selection.selectParent(population);
                Chromosome[] children = crossover.crossParents(parent1, parent2, CROSSOVER_PROBABILITY);
                mutation.mutateChild(children[0], MUTATION_PROBABILITY);
                mutation.mutateChild(children[1], MUTATION_PROBABILITY);
                newPopulation[currentAvailableIndex++] = children[0];
                newPopulation[currentAvailableIndex++] = children[1];
            }
            for (int j = 0; j < POPULATION_SIZE; j++) {
                population[j] = newPopulation[j].clone();
            }
            evaluatePopulation(population);
            sortPopulation(population);
            if (i % 10 == 0) {
                System.out.println("Generation: " + i + " , best solution: " + population[0].totalCost);
            }
        }
        System.out.println("Generation: " + NUMBER_OF_GENERATIONS + " , best solution: " + population[0].totalCost);
        this.bestSolution = population[0];
    }

    /**
     * Initialize population to some random values.
     *
     * @param population Array to be instanced with chromosomes.
     */
    private void initializePopulation(Chromosome[] population) {
        for (int i = 0; i < population.length; i++) {
            population[i] = factory.get();
            population[i].initialize();
        }
    }

    private void evaluatePopulation(Chromosome[] population) {
        for (int i = 0; i < population.length; i++) {
            population[i].totalCost = population[i].evaluate();
        }
    }
    /**
     * Sort population by their totalCost.
     * Solutions with less cost after sorting must be on lower indexes.
     *
     * @param population Population to sort.
     */
    private void sortPopulation(Chromosome[] population) {
        Arrays.sort(population, Comparator.comparingLong(o -> o.totalCost));
    }
}
