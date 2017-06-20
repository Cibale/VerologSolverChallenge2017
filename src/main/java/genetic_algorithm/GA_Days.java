package main.java.genetic_algorithm;

import main.java.ProblemModel;
import main.java.genetic_algorithm.crossover.Crossover;
import main.java.genetic_algorithm.crossover.DaysCrossover;
import main.java.genetic_algorithm.crossover.StandardCrossover;
import main.java.genetic_algorithm.evaluation.DaysEvaluation;
import main.java.genetic_algorithm.evaluation.EvaluationFunction;
import main.java.genetic_algorithm.evaluation.StandardEvaluation;
import main.java.genetic_algorithm.mutation.DaysMutation;
import main.java.genetic_algorithm.mutation.Mutation;
import main.java.genetic_algorithm.mutation.StandardMutation;
import main.java.genetic_algorithm.selection.DaysTournament;
import main.java.genetic_algorithm.selection.Selection;
import main.java.genetic_algorithm.selection.TournamentSelection;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by mmatak on 6/16/17.
 */
public class GA_Days {
    public DaysChromosome bestSolution;
    // must be even because of start method
    public static int POPULATION_SIZE = 50;
    public static int NUMBER_OF_GENERATIONS = 1000;
    public static double CROSSOVER_PROBABILITY = 0.85;
    public static double MUTATION_PROBABILITY = 0.1;
    public static int K_TOURNAMENT_SELECTION = 3;
    public DaysChromosome[] population;
    private DaysEvaluation evaluationFunction;
    private DaysCrossover crossover;
    private DaysMutation mutation;
    private DaysTournament selection;
    private ProblemModel model;


    public GA_Days(ProblemModel model) {
        this.model = model;
        population = new DaysChromosome[POPULATION_SIZE];
        evaluationFunction = new DaysEvaluation(model);
        crossover = new DaysCrossover();
        mutation = new DaysMutation();
        selection = new DaysTournament(K_TOURNAMENT_SELECTION, evaluationFunction);
    }

    public void start() {
        initializePopulation(population);
        evaluationFunction.evaluatePopulation(population);
        sortPopulation(population);
        DaysChromosome[] newPopulation = new DaysChromosome[population.length];

        for (int i = 0; i <= NUMBER_OF_GENERATIONS; i++) {
            // best solutions are on the lower indexes
            newPopulation[0] = population[0];
            newPopulation[1] = population[1];
            int currentAvailableIndex = 2;
            for (int j = 0; j < POPULATION_SIZE / 2 - 1; j++) {
                DaysChromosome parent1 = selection.selectParent(population);
                DaysChromosome parent2 = selection.selectParent(population);
                DaysChromosome[] children = crossover.crossParents(parent1, parent2, CROSSOVER_PROBABILITY);
                mutation.mutateChild(children[0], MUTATION_PROBABILITY);
                mutation.mutateChild(children[1], MUTATION_PROBABILITY);
                newPopulation[currentAvailableIndex++] = children[0];
                newPopulation[currentAvailableIndex++] = children[1];
            }
            for (int j = 0; j < POPULATION_SIZE; j++) {
                population[j] = new DaysChromosome(newPopulation[j]);
            }
            evaluationFunction.evaluatePopulation(population);
            sortPopulation(population);
            if (i % 10 == 0) {
                System.out.println("Generation: " + i + " , best solution: " + population[0].totalCost+", "+population[0].realCost);
            }
        }
        System.out.println("Generation: " + GA_Days.NUMBER_OF_GENERATIONS + " , best solution: " + population[0].totalCost);
        this.bestSolution = population[0];
    }

    /**
     * Initialize population to some random values.
     *
     * @param population Array to be instanced with chromosomes.
     */
    private void initializePopulation(DaysChromosome[] population) {
        for (int i = 0; i < population.length; i++) {
            population[i] = new DaysChromosome(model);
            population[i].initialize();
        }
    }

    /**
     * Sort population by their totalCost.
     * Solutions with less cost after sorting must be on lower indexes.
     *
     * @param population Population to sort.
     */
    private void sortPopulation(DaysChromosome[] population) {
        Arrays.sort(population, Comparator.comparingInt(o -> o.totalCost));
    }
}
