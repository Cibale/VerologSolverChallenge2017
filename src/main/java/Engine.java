package main.java;

import main.java.genetic_algorithm.Chromosome;
import main.java.genetic_algorithm.GA;
import main.java.output.Solution;

import java.util.Random;

/**
 * Created by mmatak on 6/15/17.
 */
public class Engine {
    public ProblemModel model;
    public Solution bestSolution;
    public Random rand = new Random();

    public Engine(ProblemModel model) {
        this.model = model;
    }

    public void decideDays() {
        // random method just to start with
        for (int i = 0; i < model.requests.length; i++) {
            Request request = model.requests[i];
            request.pickedDayForDelivery = request.firstDayForDelivery +
                    (rand.nextInt(request.lastDayForDelivery + 1) - request.firstDayForDelivery);
        }
        createNegativeRequests();
    }

    /**
     * Once when day of delivery of all requests is decided,
     * we also need to create negative requests,
     * i.e. requests for picking up those tools from customers.
     */
    private void createNegativeRequests() {
        model.negativeRequests = new Request[model.requests.length];
        for (int i = 0; i < model.negativeRequests.length; i++) {
            model.negativeRequests[i] = model.requests[i].getNegativeRequest();
        }
    }

    public void run() {
        GA ga = new GA(model);
        ga.start();
        bestSolution = new Solution(model);
        bestSolution.constructFrom(ga.bestSolution);
    }

}
