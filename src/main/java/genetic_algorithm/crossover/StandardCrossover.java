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
        Chromosome child1 = new Chromosome(parent1);
        Chromosome child2 = new Chromosome(parent2);
        for (int i = 0; i < splitPoint; i++) {
            child1.requests[i].correspondingVehicle.removeRequest(child1.requests[i]);
            child1.requests[i].correspondingVehicle = parent2.requests[i].correspondingVehicle;
            child1.requests[i].correspondingVehicle.addRequest(child1.requests[i]);

            child2.requests[i].correspondingVehicle.removeRequest(child2.requests[i]);
            child2.requests[i].correspondingVehicle = parent1.requests[i].correspondingVehicle;
            child2.requests[i].correspondingVehicle.addRequest(child2.requests[i]);
        }
        for (int i = 0; i < parent1.vehicles.length; i++){
            child1.vehicles[i].optimizeRoute();
            child2.vehicles[i].optimizeRoute();
        }

        return new Chromosome[]{child1, child2};
    }
}
