package main.java.genetic_algorithm.mutation;

import main.java.genetic_algorithm.Chromosome;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by felentovic on 16.06.17..
 */
public class StandardMutation extends Mutation {

    @Override
    public void mutateChild(Chromosome child, double MUTATION_PROBABILITY) {
        for (int i = 0; i < child.getChromosomeLength(); i++) {
            if (Math.random() > MUTATION_PROBABILITY) {
                continue;
            }
            int randIndex = ThreadLocalRandom.current().nextInt(child.getChromosomeLength());

            child.setGenomeValue(randIndex, child.getGenomeValue(randIndex));

        }
        child.update();

    }
}
