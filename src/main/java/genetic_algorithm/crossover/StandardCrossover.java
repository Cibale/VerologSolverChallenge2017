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
            return new Chromosome[]{new Chromosome(parent1), new Chromosome(parent2)};
        }
        int splitPoint = ThreadLocalRandom.current().nextInt(parent1.requests.length);
        Chromosome child1 = new Chromosome(parent1);
        Chromosome child2 = new Chromosome(parent2);
        for (int i = 0; i < splitPoint; i++) {

            child1.vehicles[child1.requests[i].correspondingVehicleId].removeRequest(i);
            child1.requests[i].correspondingVehicleId = parent2.requests[i].correspondingVehicleId;
            child1.vehicles[child1.requests[i].correspondingVehicleId].addRequest(i);



            child2.vehicles[child2.requests[i].correspondingVehicleId].removeRequest(i);
            child2.requests[i].correspondingVehicleId = parent1.requests[i].correspondingVehicleId;
            child2.vehicles[child2.requests[i].correspondingVehicleId].addRequest(i);
        }
        for (int i = 0; i < parent1.vehicles.length; i++) {
            child1.vehicles[i].update();
            child2.vehicles[i].update();
        }

        return new Chromosome[]{child1, child2};
    }
}
