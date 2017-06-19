package main.java.genetic_algorithm;

import main.java.ProblemModel;
import main.java.Request;
import main.java.Vehicle;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by mmatak on 6/16/17.
 */
public class Chromosome {
    public Vehicle[] vehicles;
    public Request[] requests;
    // less is better
    public int totalCost;
    //cost without punishments
    public int realCost;
    public ProblemModel model;

    /**
     * Hard copy chromosome.
     */
    public Chromosome(Chromosome chromosomeToCopy) {
        this(chromosomeToCopy.model, chromosomeToCopy.vehicles, chromosomeToCopy.requests);
        this.totalCost = chromosomeToCopy.totalCost;
        this.realCost = chromosomeToCopy.realCost;
    }

    public Chromosome(ProblemModel model, Vehicle[] vehicles, Request[] requests) {
        this.model = model;

        this.requests = new Request[requests.length];
        for (int i = 0; i < requests.length; i++) {
            this.requests[i] = new Request(requests[i]);
        }

        this.vehicles = new Vehicle[vehicles.length];
        for (int i = 0; i < vehicles.length; i++) {
            this.vehicles[i] = new Vehicle(vehicles[i], this);
        }


    }

    public Chromosome(ProblemModel model) {
        this.model = model;
    }

    /**
     * Initialize chromosome (vehicles and requests) to random picks.
     */
    public void initialize() {
        // in worst case - one vehicle per request
        // because of empty first request id (no request with id 0)
        vehicles = new Vehicle[model.requests.length + model.negativeRequests.length];
        requests = new Request[model.requests.length + model.negativeRequests.length];
        int index = 0;
        for (int i = 0; i < model.requests.length; i++) {
            requests[index++] = new Request(model.requests[i]);
        }
        for (int i = 0; i < model.negativeRequests.length; i++) {
            if (index != model.negativeRequests[i].id){
                System.out.println("Error line 64 in Chromosme.java - id doesn't match");
            }
            requests[index++] = new Request(model.negativeRequests[i]);
        }
        for (int i = 0; i < vehicles.length; i++) {
            vehicles[i] = new Vehicle(i, this);
        }
        for (int i = 0; i < requests.length; i++) {
            int vehicleIndex = ThreadLocalRandom.current().nextInt(vehicles.length);
            vehicles[vehicleIndex].addRequest(i);
            requests[i].correspondingVehicleId = vehicleIndex;
        }
        for (int i = 0; i < vehicles.length; i++) {
            vehicles[i].update();
        }

    }

    @Override
    public String toString() {
        return "Real cost:"+this.realCost+" Total cost:"+this.totalCost;
    }
}
