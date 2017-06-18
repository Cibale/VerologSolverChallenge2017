package main.java.genetic_algorithm.crossover;

import main.java.Request;
import main.java.Vehicle;
import main.java.genetic_algorithm.Chromosome;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Crossover with one point.
 * Created by felentovic on 16.06.17..
 */
public class StandardCrossover extends Crossover {

    @Override
    public Chromosome[] crossParents(Chromosome parent1, Chromosome parent2, double CROSSOVER_PROBABILITY) {
//        if (Math.random() > CROSSOVER_PROBABILITY) {
//            return new Chromosome[]{parent1, parent2};
//        }
        int splitPoint = ThreadLocalRandom.current().nextInt(parent1.requests.length);
        Chromosome child1 = new Chromosome(parent1);
        Chromosome child2 = new Chromosome(parent2);
        for (int i = 0; i < splitPoint; i++) {
            Request child1Request = child1.requests[i];
            Vehicle child1Vehicle = child1.vehicles[child1Request.correspondingVehicleId];

            child1Vehicle.removeRequest(i);
            child1Request.correspondingVehicleId = parent2.requests[i].correspondingVehicleId;
            child1Vehicle.addRequest(i);

            Request child2Request = child2.requests[i];
            Vehicle child2Vehicle = child2.vehicles[child2Request.correspondingVehicleId];

            child2Vehicle.removeRequest(i);
            child2Request.correspondingVehicleId = parent1.requests[i].correspondingVehicleId;
            child2Vehicle.addRequest(i);
        }
        for (int i = 0; i < parent1.vehicles.length; i++) {
            child1.vehicles[i].updateDayRoutes();
            child2.vehicles[i].updateDayRoutes();
        }

        return new Chromosome[]{child1, child2};
    }
}
