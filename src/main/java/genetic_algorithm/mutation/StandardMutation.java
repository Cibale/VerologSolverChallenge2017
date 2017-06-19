package main.java.genetic_algorithm.mutation;

import main.java.Request;
import main.java.genetic_algorithm.Chromosome;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 16.06.17..
 */
public class StandardMutation extends Mutation {

    @Override
    public void mutateChild(Chromosome child, double MUTATION_PROBABILITY) {
        for (int i = 0; i < child.requests.length; i++) {
            if (Math.random() > MUTATION_PROBABILITY ) {
                continue;
            }
                child.vehicles[child.requests[i].correspondingVehicleId].removeRequest(i);


                int randIndex = ThreadLocalRandom.current().nextInt(child.vehicles.length);
                child.vehicles[randIndex].addRequest(i);
            child.requests[i].correspondingVehicleId = child.vehicles[randIndex].id;

        }

    }
}
