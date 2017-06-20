package main.java.genetic_algorithm.crossover;

import main.java.genetic_algorithm.DaysChromosome;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 20.06.17..
 */
public class DaysCrossover {

    public DaysChromosome[] crossParents(DaysChromosome parent1, DaysChromosome parent2, double CROSSOVER_PROBABILITY) {
        if (Math.random() > CROSSOVER_PROBABILITY) {
            return new DaysChromosome[]{new DaysChromosome(parent1), new DaysChromosome(parent2)};
        }
        int splitPoint = ThreadLocalRandom.current().nextInt(parent1.requests.length);
        DaysChromosome child1 = new DaysChromosome(parent1);
        DaysChromosome child2 = new DaysChromosome(parent2);
        for (int i = 0; i < splitPoint; i++) {

            child1.detachRequest(child1.requests[i]);
            child1.assignRequestToDay(child1.requests[i], parent2.requests[i].pickedDayForDelivery);


            child2.detachRequest(child2.requests[i]);
            child2.assignRequestToDay(child2.requests[i], parent1.requests[i].pickedDayForDelivery);
        }

        return new DaysChromosome[]{child1, child2};
    }
}
