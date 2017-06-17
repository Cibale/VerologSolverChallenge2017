package main.java.genetic_algorithm.mutation;

import main.java.genetic_algorithm.Chromosome;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 16.06.17..
 */
public class StandardMutation extends Mutation {

    @Override
    public void mutateChild(Chromosome child, double MUTATION_PROBABILITY) {
          for (int i = 0; i < child.requests.length; i++){
              if (Math.random() < MUTATION_PROBABILITY){
                  int randIndex = ThreadLocalRandom.current().nextInt(child.vehicles.length);
                  child.vehicles[randIndex].removeRequest(child.requests[i]);
                  child.requests[i].corespondingVehicle = child.vehicles[randIndex];
                  child.vehicles[randIndex].addRequest(child.requests[i]);
              }
          }

    }
}