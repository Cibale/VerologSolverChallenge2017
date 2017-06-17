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
            return new Chromosome[]{parent1, parent2};
        }
        int splitPoint = ThreadLocalRandom.current().nextInt(parent1.requests.length);
        Chromosome child1 = new Chromosome(parent1.vehicles, parent1.requests);
        Chromosome child2 = new Chromosome(parent2.vehicles, parent2.requests);
        for (int i = 0; i < splitPoint; i++) {
            child1.requests[i].corespondingVehicle.removeRequest(child1.requests[i]);
            child1.requests[i].corespondingVehicle = parent2.requests[i].corespondingVehicle;
            child1.requests[i].corespondingVehicle.addRequest(child1.requests[i]);

            child2.requests[i].corespondingVehicle.removeRequest(child2.requests[i]);
            child2.requests[i].corespondingVehicle = parent1.requests[i].corespondingVehicle;
            child2.requests[i].corespondingVehicle.addRequest(child2.requests[i]);
        }

        return new Chromosome[]{child1, child2};
    }
}
