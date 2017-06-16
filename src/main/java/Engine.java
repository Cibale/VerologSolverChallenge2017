package main.java;

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
        for (int i = 0; i < model.requests.length; i++) {
            Request request = model.requests[i];
            Request negativeRequest = new Request(request);
            model.negativeRequests[i] = negativeRequest;
        }
    }

    public void run() {
        GA ga = new GA();
        ga.start(model);
        constructSolution(ga.bestSolution, model);
    }


}
