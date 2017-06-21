package main.java.genetic_algorithm.crossover;

import main.java.Request;
import main.java.genetic_algorithm.DaysChromosome;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 20.06.17..
 */
public class DaysCrossover {

    public DaysChromosome[] crossParents(DaysChromosome parent1, DaysChromosome parent2, double CROSSOVER_PROBABILITY) {
        if (Math.random() > CROSSOVER_PROBABILITY) {
            return new DaysChromosome[]{new DaysChromosome(parent1), new DaysChromosome(parent2)};
        }
        DaysChromosome child1 = new DaysChromosome(parent1);
        DaysChromosome child2 = new DaysChromosome(parent2);

        crossChild(child1,parent1,parent2);
        crossChild(child2,parent2,parent1);

        return new DaysChromosome[]{child1, child2};
    }

    private void crossChild(DaysChromosome child1, DaysChromosome parent1, DaysChromosome parent2){
        double probability = 0.6;

        if(Math.random() > probability && parent1.sumExceeded != 0) {
            DaysChromosome.ExceededToolId exceededToolId1 = parent1.proportionalySelectExceedToolId();
            List<Request> linkedRequests1 = child1.daysMap.get(exceededToolId1.day).get(exceededToolId1.toolId).requestList;
            int splitted = ThreadLocalRandom.current().nextInt(linkedRequests1.size());
            // splitted = linkedRequests1.size();
            for (int i = 0; i < splitted; i++) {
                child1.detachRequest(child1.requests[i]);
                child1.assignRequestToDay(child1.requests[i], parent2.requests[i].pickedDayForDelivery);
            }
        }else {
            int splitPoint = ThreadLocalRandom.current().nextInt(parent1.requests.length);
            for (int i = 0; i < splitPoint; i++) {
                child1.detachRequest(child1.requests[i]);
                child1.assignRequestToDay(child1.requests[i], parent2.requests[i].pickedDayForDelivery);
            }

        }
    }
}
