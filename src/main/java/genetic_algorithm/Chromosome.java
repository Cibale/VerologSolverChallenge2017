package main.java.genetic_algorithm;

import main.java.ProblemModel;
import main.java.Request;
import main.java.Vehicle;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by mmatak on 6/16/17.
 */
public class Chromosome {
    public Vehicle[] vehicles;
    public Request[] requests;
    // less is better
    public int totalCost;
    public ProblemModel model;

    /**
     * Hard copy chromosome.
     */
    public Chromosome(Chromosome chromosomeToCopy) {
        this(chromosomeToCopy.model, chromosomeToCopy.vehicles, chromosomeToCopy.requests);
        this.totalCost = chromosomeToCopy.totalCost;
    }

    public Chromosome(ProblemModel model, Vehicle[] vehicles, Request[] requests) {
        this.model = model;
        this.vehicles = Arrays.copyOf(vehicles, vehicles.length);
        this.requests = Arrays.copyOf(requests, requests.length);
    }

    public Chromosome(ProblemModel model) {
        this(model, null, null);
    }

    /**
     * Initialize chromosome (vehicles and requests) to random picks.
     */
    public void initialize() {
        // in worst case - one vehicle per request
        vehicles = new Vehicle[model.requests.length + model.negativeRequests.length];
        requests = new Request[model.requests.length + model.negativeRequests.length];
        int index = 0;
        for (int i = 0; i < model.requests.length; i++) {
            requests[index++] = new Request(model.requests[i]);
        }
        for (int i = 0; i < model.negativeRequests.length; i++) {
            requests[index++] = new Request(model.negativeRequests[i]);
        }
        for (int i = 0; i < vehicles.length; i++) {
            vehicles[i] = new Vehicle();
        }
        for (int i = 0; i < requests.length; i++){
            int vehicleIndex = ThreadLocalRandom.current().nextInt(vehicles.length);
            requests[i].correspondingVehicle = vehicles[vehicleIndex];
            vehicles[vehicleIndex].addRequest(requests[i]);
        }
    }
}
