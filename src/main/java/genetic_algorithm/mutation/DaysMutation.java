package main.java.genetic_algorithm.mutation;

import main.java.Request;
import main.java.genetic_algorithm.DaysChromosome;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 20.06.17..
 */
public class DaysMutation {

    public void mutateChild(DaysChromosome child, double MUTATION_PROBABILITY) {
        double probability = 0.6;
        DaysChromosome tmpChild = new DaysChromosome(child);
        if (Math.random() > probability && child.sumExceeded != 0) {
            DaysChromosome.ExceededToolId exceededToolId = child.proportionalySelectExceedToolId();
            List<Request> linkedRequests = tmpChild.daysMap.get(exceededToolId.day).get(exceededToolId.toolId).requestList;
            for (Request request : linkedRequests) {
                if (Math.random() > MUTATION_PROBABILITY) {
                    continue;
                }
                child.detachRequest(request);
                child.assignRequestToDay(request, request.firstDayForDelivery +
                        ThreadLocalRandom.current().nextInt(request.lastDayForDelivery + 1 - request.firstDayForDelivery));
            }
        } else {
            for (Request request : child.requests) {
                if (Math.random() > MUTATION_PROBABILITY) {
                    continue;
                }
                child.detachRequest(request);
                child.assignRequestToDay(request, request.firstDayForDelivery +
                        ThreadLocalRandom.current().nextInt(request.lastDayForDelivery + 1 - request.firstDayForDelivery));


            }

        }
    }
}
