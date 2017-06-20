package main.java.genetic_algorithm.mutation;

import main.java.Request;
import main.java.genetic_algorithm.Chromosome;
import main.java.genetic_algorithm.DaysChromosome;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 20.06.17..
 */
public class DaysMutation {

    public void mutateChild(DaysChromosome child, double MUTATION_PROBABILITY) {
        for (Request request : child.requests) {
            if (Math.random() > MUTATION_PROBABILITY ) {
                continue;
            }

            child.detachRequest(request);
            child.assignRequestToDay(request, request.firstDayForDelivery +
                    ThreadLocalRandom.current().nextInt(request.lastDayForDelivery + 1 - request.firstDayForDelivery));


        }
    }
}
